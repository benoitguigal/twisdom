package jobs

import akka.actor.Actor
import org.joda.time.DateTime
import db.{MongoStore, StatsMapReduce}
import scala.concurrent.duration._
import scala.concurrent.Await


object QuotationStatsActor {
  case object IncrementalMapReduce
  case object MapReduceDone
}

import QuotationStatsActor._

class QuotationStatsActor extends Actor {

  context.system.scheduler.schedule(12 hours, 12 hours, self, IncrementalMapReduce)

  var lastUpdate = new DateTime()

  val mapReduce = StatsMapReduce()
  val store = new MongoStore

  implicit val exec = context.dispatcher

  override def receive = {
    case IncrementalMapReduce =>
      mapReduce.run(lastUpdate.getMillis).onSuccess { case _ => context.self ! MapReduceDone }
    case MapReduceDone =>
      lastUpdate = new DateTime()
      store.removeQuotations()
  }

  override def postStop() = {
    val backupBeforeShutdown = mapReduce.run(lastUpdate.getMillis) flatMap { _ => store.removeQuotations() }
    Await.result(backupBeforeShutdown, 10 seconds)
    super.postStop()
  }
}
