package db

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection
import models.Quotation
import reactivemongo.bson.BSONDocument
import scala.concurrent.Future

object MongoProxy {

  private[this] val config = MongoConfig()
  private[this] val driver = new MongoDriver
  private[this] val connection = driver.connection(List(config.host))
  private[this] val db = connection(config.db)
  val quotationsCollection = db[BSONCollection]("quotations")

  def lastQuotation: Future[Option[Quotation]] = quotationsCollection
    .find(BSONDocument())
    .sort(BSONDocument("tweetCreatedAt" -> -1))
    .cursor[Quotation]
    .collect[List](1)
    .map(_.headOption)
}



