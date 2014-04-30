package jobs

import akka.actor.Actor
import twitter4j.Status
import models.{QuotationExtractor, SimpleStatus, UnknownAuthor, Quotation}
import play.api.libs.iteratee.{Enumerator, Concurrent}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import db.MongoStore


object QuotationExtractorActor {
  case object Connect
  case class Connected(enumerator: Enumerator[Option[(Quotation, SimpleStatus)]])
  case object Refresh
  case object GetMostRecentQuotation
}

import QuotationExtractorActor._

class QuotationExtractorActor
  extends Actor
  with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  val twitterStream = new TwitterStream
  twitterStream.register(context.self)

  val extractor = new QuotationExtractor

  val store = new MongoStore

  var mostRecent: Option[(Quotation, SimpleStatus)] = None

  val (enumerator, channel) = Concurrent.broadcast[Option[(Quotation, SimpleStatus)]]

  implicit val exec = context.dispatcher

  def receive: Receive = {
    case status: Status =>
      val simpleStatus = SimpleStatus(status)
      extractor(simpleStatus) match {
        case Some(quotation) if (quotation.author != UnknownAuthor) =>
          store.insertQuotation(quotation)
          store.insertUser(simpleStatus.user)
          if (quotation.lang == "en") { mostRecent = Some(quotation, simpleStatus) }
        case _ =>
      }
    case Connect => sender ! Connected(enumerator)
    case Refresh => channel.push(mostRecent)
    case GetMostRecentQuotation => sender ! mostRecent
  }


  override def postStop() = {
    twitterStream.shutdown()
    super.postStop()
  }
}
