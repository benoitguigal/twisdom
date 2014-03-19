package jobs

import db.QuotationCollectionProxy
import models.Quotation
import akka.actor.{ActorLogging, Actor}
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executor


object QuotationBackuper {
  case object BackupFinished
  final val parallelism = 10  ///TODO fine tune this number. It seems ReactiveMongo opens 10 concurrent connections
}

import QuotationBackuper._

class QuotationBackuper extends Actor with ActorLogging {

  var processing = 0  ///TODO use a Vector[Job] with unique job id

  val quotationCollectionProxy = QuotationCollectionProxy.default

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  override def receive = {
    case q: Quotation if (processing <= parallelism) =>
      processing = processing + 1
      quotationCollectionProxy.updateOrInsert(q).onComplete(_ => context.self ! BackupFinished)
    case q: Quotation => log info ("Too busy to process quotation")
    case BackupFinished => processing = processing - 1
  }

}
