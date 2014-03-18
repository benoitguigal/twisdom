package db

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection
import models.Quotation
import reactivemongo.bson.BSONDocument
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import reactivemongo.api.DB


object MongoProxy {

  val default = new MongoProxy(ReactiveMongoPlugin.db)
  def apply() = default
}

class MongoProxy(db: DB) {

  val quotationsCollection = db[BSONCollection]("quotations")

  def lastQuotation: Future[Option[Quotation]] = quotationsCollection
    .find(BSONDocument())
    .sort(BSONDocument("tweetCreatedAt" -> -1))
    .cursor[Quotation]
    .collect[List](1)
    .map(_.headOption)

  def updateOrInsert(quotation: Quotation) = {
    val query = BSONDocument("quote" -> quotation.quote)
    quotationsCollection.find(query).one[Quotation] flatMap {
      case Some(q) => {
        val update = q.copy(statuses = q.statuses ++ quotation.statuses)
        quotationsCollection.update(BSONDocument("_id" -> q.id), update)
      }
      case None => {
        quotationsCollection.insert(quotation)
      }
    }
  }

  def flush() = quotationsCollection.drop()

}



