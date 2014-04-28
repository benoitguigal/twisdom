package jobs

import akka.actor.{ActorLogging, Props, Actor}
import twitter4j.Status
import models.{SimpleStatus, UnknownAuthor, Author, Quotation}
import models.CaseInsensitiveString
import play.api.libs.iteratee.{Enumerator, Concurrent}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}


object QuotationExtractor {

  private[this] val QuotationRE = ".*\"([^@#/\"]{20,})\".*".r

  def apply(status: SimpleStatus): Option[Quotation] = {

    lazy val author = {
      val foundInList = Author.defaults.find(a => status.text.containsCaseInsensitive(a.name))
      foundInList.getOrElse(UnknownAuthor)
    }

    status.text match {
      case QuotationRE(q) if (!q.contains(author.name)) =>
        Some(Quotation(None, q, author, Seq(status.createdAt)))
      case _ => None
    }
  }

  case object Connect
  case class Connected(enumerator: Enumerator[Option[(Quotation, SimpleStatus)]])
  case object Refresh
  case object GetMostRecentQuotation
}


import QuotationExtractor._

class QuotationExtractor extends Actor with ActorLogging with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  context.actorOf(Props[TwitterStreamListener])
  val backuper = context.actorOf(Props[QuotationBackuper])

  var mostRecent: Option[(Quotation, SimpleStatus)] = None

  val (enumerator, channel) = Concurrent.broadcast[Option[(Quotation, SimpleStatus)]]

  def receive: Receive = {
    case status: Status =>
      val simpleStatus = SimpleStatus(status)
      QuotationExtractor(simpleStatus) match {
        case Some(quotation) if (quotation.author != UnknownAuthor) =>
          mostRecent = Some(quotation, simpleStatus)
          backuper ! quotation
        case _ =>
      }
    case Connect => sender ! Connected(enumerator)
    case Refresh => channel.push(mostRecent)
    case GetMostRecentQuotation => sender ! mostRecent
  }

}
