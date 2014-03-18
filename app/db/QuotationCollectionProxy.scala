package db

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection
import models.Quotation
import reactivemongo.bson.{BSONArray, BSONDocument}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import reactivemongo.api.DB



object QuotationCollectionProxy {
  val default = new QuotationCollectionProxy(ReactiveMongoPlugin.db)
}

class QuotationCollectionProxy(db: DB) {

  val quotationCollection = db[BSONCollection]("quotations")

  def updateOrInsert(quotation: Quotation) = {
    val query = BSONDocument("quote" -> quotation.quote)
    quotationCollection.find(query).one[Quotation] flatMap {
      case Some(q) => {
        val update = q.copy(statuses = q.statuses ++ quotation.statuses)
        quotationCollection.update(BSONDocument("_id" -> q.id), update)
      }
      case None => {
        quotationCollection.insert(quotation)
      }
    }
  }

  def flush() = quotationCollection.remove(BSONDocument())

  /**
   * keep the n most popular quotations
   * @param n
   */
  def keep(n: Int) = {

    val mostPopular = quotationCollection
      .find(BSONDocument())
      .sort(BSONDocument("statusesCount" -> -1))
      .cursor[Quotation]
      .collect[List](n)   ///TODO use enumerate() here
      .map(_.map(_.id.get))

    mostPopular flatMap { ids =>
      val deleteQuery = BSONDocument("_id" -> BSONDocument("$nin" -> BSONArray(ids)))
      quotationCollection.remove(deleteQuery)
    }
  }

}



