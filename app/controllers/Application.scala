package controllers

import play.api.mvc._
import jobs.{QuotationStatsActor, QuotationExtractorActor}
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
import models.{SimpleStatus, Quotation, QuotationAndStatusJSONWriter}
import jobs.QuotationExtractorActor.{Connected, Connect, GetMostRecentQuotation, Refresh}



object Application extends Controller with DefaultWriteables {

  val extractor = Akka.system.actorOf(Props[QuotationExtractorActor], name = "quotationExtractor")
  Akka.system.scheduler.schedule(0 seconds, 5 seconds) { extractor ! Refresh }

  Akka.system.actorOf(Props[QuotationStatsActor], name = "quotationStats")

  def index = Action.async { implicit request =>

    implicit val timeout = Timeout(10 seconds)
    (extractor ? GetMostRecentQuotation).mapTo[Option[(Quotation, SimpleStatus)]] map {
      case Some((q, s)) =>
        val streamView = views.html.stream(q, s)
        Ok(views.html.main(streamView))
      case _ => NoContent
    }
  }

  def stream = WebSocket.async[JsValue] { request =>

    implicit val timeout = Timeout(10 seconds)

    (extractor ? Connect) map {
      case Connected(enumerator) =>
        (Iteratee.ignore[JsValue], enumerator map (toJson(_)))
    }
  }

}