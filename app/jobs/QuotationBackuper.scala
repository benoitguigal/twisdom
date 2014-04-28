package jobs

import db.QuotationCollectionProxy
import models.Quotation
import akka.actor.{ActorLogging, Actor}
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executor
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}


class QuotationBackuper extends Actor with ActorLogging with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  val quotationCollectionProxy = QuotationCollectionProxy.default

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  override def receive = {
    case q: Quotation =>
      quotationCollectionProxy.updateOrInsert(q)
  }

}
