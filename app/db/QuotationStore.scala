package db

import models.Quotation
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext


class QuotationStore extends Mongo {

  private val collection = db[BSONCollection]("raw_quotations")
  def insert(q: Quotation)(implicit exec: ExecutionContext) = collection.insert(q)

}
