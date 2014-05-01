package db

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current


trait Mongo {

  def db = ReactiveMongoPlugin.db
}
