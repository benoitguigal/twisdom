package db

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current


trait Mongo {

  def driver = ReactiveMongoPlugin.driver

  def connection = ReactiveMongoPlugin.connection

  def db = ReactiveMongoPlugin.db
}
