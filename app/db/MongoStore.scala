package db

import models.{SimpleUser, Quotation}
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext


class MongoStore extends Mongo {

  private val collectionQ = db[BSONCollection]("raw_quotations")
  def insertQuotation(q: Quotation)(implicit exec: ExecutionContext) = collectionQ.insert(q)

  private val collectionU = db[BSONCollection]("raw_users")
  def insertUser(u: SimpleUser)(implicit exec: ExecutionContext) = collectionU.insert(u)

}
