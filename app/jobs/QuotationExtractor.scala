package jobs

import akka.actor.{ActorLogging, Props, Actor}
import twitter4j.Status
import models.{SimpleStatus, UnknownAuthor, Author, Quotation}
import models.CaseInsensitiveString


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

  case object GetMostRecentQuotation
}


import QuotationExtractor._

class QuotationExtractor extends Actor with ActorLogging {

  context.actorOf(Props[TwitterStreamListener])
  val backuper = context.actorOf(Props[QuotationBackuper])

  var mostRecent: Option[(Quotation, SimpleStatus)] = None

  def receive: Receive = {
    case status: Status =>
      val simpleStatus = SimpleStatus(status)
      QuotationExtractor(simpleStatus) match {
        case Some(quotation) if (quotation.author != UnknownAuthor) =>
          mostRecent = Some(quotation, simpleStatus)
          backuper ! quotation
        case _ =>
      }
    case GetMostRecentQuotation => sender ! mostRecent
  }

}
