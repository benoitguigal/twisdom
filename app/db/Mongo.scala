package db

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import reactivemongo.api.collections.default.BSONCollection


trait Mongo {

  protected def db = ReactiveMongoPlugin.db
  protected val rawQuotationsColl = db[BSONCollection]("raw_quotations")
  protected val rawUsersColl = db[BSONCollection]("raw_users")
  protected val statsQuotationsColl = db[BSONCollection]("stats_quotations")
}
