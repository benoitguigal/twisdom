package jobs

import akka.actor.{Actor, ActorLogging}
import db.MongoProxy
import reactivemongo.bson.BSONDocument
import models.Quotation
import Quotation.QuotationBSONReader
import play.api.libs.concurrent.Execution.Implicits.defaultContext



class LastQuotationActor extends Actor with ActorLogging {

  var lastQuotation: Option[Quotation] = None

  def receive = {
    case UpdateQuotation => updateLastQuotation
    case GetLastQuotation =>  sender ! lastQuotation
    case m => log.info(s"Got unknown message: $m")
  }


  def updateLastQuotation: Unit = {

    val lastQuotationOpt = MongoProxy.quotationsCollection
      .find(BSONDocument())
      .sort(BSONDocument("tweetCreatedAt" -> -1))
      .cursor[Quotation]
      .collect[List](1)

    lastQuotationOpt onSuccess { case q => lastQuotation = q.headOption }
    lastQuotationOpt onFailure { case e => log.error(e, e.getMessage) }

  }
}

case object UpdateQuotation
case object GetLastQuotation
