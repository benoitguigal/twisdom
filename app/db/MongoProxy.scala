package db

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection

object MongoProxy {

  private[this] val config = MongoConfig()
  private[this] val driver = new MongoDriver
  private[this] val connection = driver.connection(List(config.host))
  private[this] val db = connection(config.db)
  val collection = db[BSONCollection]("tweets")
}



