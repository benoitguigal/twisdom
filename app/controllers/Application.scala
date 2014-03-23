package controllers

import play.api.mvc._
import jobs.QuotationExtractor
import jobs.QuotationExtractor.{Connected, Connect, Refresh}
import akka.actor.Props
import akka.pattern.ask
import play.api.Play.current
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.http.DefaultWriteables
import play.api.libs.concurrent._
import db.QuotationCollectionProxy.default._
import models.QuotationAndStatusJSONWriter


object Application extends Controller with DefaultWriteables {

  Akka.system.scheduler.schedule(1 hour, 1 hour) { keep(1000) } // prevent the database from growing too big

  val extractor = Akka.system.actorOf(Props[QuotationExtractor], name = "quotationExtractor")
  Akka.system.scheduler.schedule(0 seconds, 5 seconds) { extractor ! Refresh }


  def stream = WebSocket.async[JsValue] { request =>

    implicit val timeout = Timeout(1 seconds)

    (extractor ? Connect) map {
      case Connected(enumerator) =>
        (Iteratee.ignore[JsValue], enumerator map (toJson(_)))
    }
  }

  def popular = Action.async {
    implicit val timeout = Timeout(2 seconds)
    mostPopular(50) map (qs => Ok(toJson(qs)))
  }

  def trending = Action.async {
    implicit val timeout = Timeout(2 seconds)
    mostTrending(50) map (qs => Ok(toJson(qs)))
  }

}