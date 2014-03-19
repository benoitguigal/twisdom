package jobs

import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import twitter4j.Status
import models.{SimpleStatus, UnknownAuthor, Author, Quotation}
import models.CaseInsensitiveString


object QuotationExtractor {

  private[this] val QuotationRE = ".*\"([^@#/\"]{20,})\".*".r

  def apply(status: twitter4j.Status): Option[Quotation] = {

    lazy val author = {
      val foundInList = Author.defaults.find(a => status.getText.containsCaseInsensitive(a.name))
      foundInList.getOrElse(UnknownAuthor)
    }

    status.getText match {
      case QuotationRE(q) if (!q.contains(author.name)) =>
        Some(Quotation(None, q, author, Seq(SimpleStatus(status))))
      case _ => None
    }
  }

  case object GetMostRecentQuotation
}


import QuotationExtractor._

class QuotationExtractor extends Actor with ActorLogging {

  context.actorOf(Props[TwitterStreamListener])
  val backuper = context.actorOf(Props[QuotationBackuper])

  var mostRecentQuotation: Option[Quotation] = None

  def receive: Receive = {
    case status: Status =>
      QuotationExtractor(status) match {
        case Some(quotation) if (quotation.author != UnknownAuthor) =>
          mostRecentQuotation = Some(quotation)
          backuper ! quotation
        case _ =>
      }
    case GetMostRecentQuotation => sender ! mostRecentQuotation
  }

}
