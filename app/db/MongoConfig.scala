package db

import play.api.Play

object MongoConfig {

  def apply(): MongoConfig = {
    val host = Play.current.configuration.getString("mongo.url").get
    val db = Play.current.configuration.getString("mongo.dbname").get
    MongoConfig(host, db)
  }

}

case class MongoConfig(host: String, db: String)