package controllers

import play.api.mvc._
import jobs.QuotationExtractor
import jobs.QuotationExtractor.GetMostRecentQuotation
import akka.actor.Props
import akka.pattern.ask
import play.api.Play.current
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import models.{SimpleStatus, Quotation}
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.http.DefaultWriteables
import play.api.libs.concurrent._
import db.QuotationCollectionProxy.default._
import models.QuotationAndStatusJSONWriter




object Application extends Controller with DefaultWriteables {

  val extractor = Akka.system.actorOf(Props[QuotationExtractor], name = "quotationExtractor")
  Akka.system.scheduler.schedule(1 hour, 1 hour) { keep(1000) } // prevent the database from growing too big

  def index = Action {
    Ok(views.html.index())
  }

  def share = Action.async {
    val popular = mostPopular(50)
    val trending = mostTrending(50)
    popular flatMap { p =>
      trending map { t =>
        Ok(views.html.share(p, t))
      }
    }
  }

  def stream = WebSocket.using[JsValue] { request =>

    implicit val timeout = Timeout(1 seconds)

    val in = Iteratee.getChunks[JsValue]

    val out: Enumerator[JsValue] = Enumerator.repeatM {
      val p = scala.concurrent.Promise[Option[(Quotation, SimpleStatus)]]()
      Akka.system.scheduler.scheduleOnce(5 seconds) {
        val mostRecentQuotation = (extractor ? GetMostRecentQuotation).mapTo[Option[(Quotation, SimpleStatus)]]
        p.completeWith(mostRecentQuotation)
      }
      p.future map (toJson(_))
    }

    (in, out)
  }

  def lastQuotation = Action.async {
    implicit val timeout = Timeout(1 seconds)
    val lastQuotationFut = (extractor ? GetMostRecentQuotation).mapTo[Option[(Quotation, SimpleStatus)]]
    lastQuotationFut map {q => Ok(toJson(q)) }
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