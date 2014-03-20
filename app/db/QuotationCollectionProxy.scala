package db

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.default.BSONCollection
import models.Quotation
import reactivemongo.bson.{BSONArray, BSONDocument}
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import reactivemongo.api.DB
import scala.concurrent.Future


object QuotationCollectionProxy {
  val default = new QuotationCollectionProxy(ReactiveMongoPlugin.db)
}

class QuotationCollectionProxy(db: DB) {

  val quotationCollection = db[BSONCollection]("quotations")

  /**
   * Check if the quotation is already present. If yes, update with a new field statuses, else insert the quotation
   * @param quotation
   * @return
   */
  def updateOrInsert(quotation: Quotation) = {
    val query = BSONDocument("quote" -> quotation.quote)
    quotationCollection.find(query).one[Quotation] flatMap {
      case Some(q) => {
        val update = q.merge(quotation)
        quotationCollection.update(BSONDocument("_id" -> q.id), update)
      }
      case None => {
        quotationCollection.insert(quotation)
      }
    }
  }

  /**
   * Empty the collection
   * @return
   */
  def flush() = quotationCollection.remove(BSONDocument())

  /**
   * keep the n most popular quotations
   * @param n
   */
  def keep(n: Int) = {

    mostPopular(n) flatMap { quotations =>
      val ids = quotations.map(_.id.get)
      val deleteQuery = BSONDocument("_id" -> BSONDocument("$nin" -> BSONArray(ids)))
      quotationCollection.remove(deleteQuery)
    }
  }

  /**
   * Retrieve the n most popular quotations
   * @param n
   * @return
   */
  def mostPopular(n: Int): Future[List[Quotation]] = {

    quotationCollection
      .find(BSONDocument())
      .sort(BSONDocument("popularity" -> -1))
      .cursor[Quotation]
      .collect[List](n)

  }

}



