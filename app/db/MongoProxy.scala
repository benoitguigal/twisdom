package db

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection
import models.Quotation
import reactivemongo.bson.BSONDocument
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current


object MongoProxy {

  private[this] val db = ReactiveMongoPlugin.db
  val quotationsCollection = db[BSONCollection]("quotations")

  def lastQuotation: Future[Option[Quotation]] = quotationsCollection
    .find(BSONDocument())
    .sort(BSONDocument("tweetCreatedAt" -> -1))
    .cursor[Quotation]
    .collect[List](1)
    .map(_.headOption)
}



