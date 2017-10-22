package jobs

import akka.actor.Actor
import org.joda.time.DateTime
import db.{Mongo, StatsMapReduce}
import scala.concurrent.duration._
import scala.concurrent.Await
import reactivemongo.bson.BSONDocument


object QuotationStatsActor {
  case object IncrementalMapReduce
  case object MapReduceDone
}

import QuotationStatsActor._

class QuotationStatsActor extends Actor with Mongo {

  var lastUpdate = new DateTime()

  val mapReduce = StatsMapReduce()

  implicit val exec = context.dispatcher

  def receive = {
    case IncrementalMapReduce =>
      mapReduce.run(lastUpdate.getMillis).onSuccess { case _ => context.self ! MapReduceDone }
    case MapReduceDone =>
      lastUpdate = new DateTime()
      rawQuotationsColl.remove(BSONDocument())
  }

  override def postStop() = {
    val backupBeforeShutdown = mapReduce.run(lastUpdate.getMillis) flatMap { _ => rawQuotationsColl.remove(BSONDocument()) }
    Await.result(backupBeforeShutdown, 10 seconds)
    super.postStop()
  }
}
