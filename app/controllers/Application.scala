package controllers

import play.api.mvc._
import jobs.{LastQuotation, TwitterStreamingActor}
import akka.actor.Props
import akka.pattern.ask
import play.api.Play.current
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import models.Quotation
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.http.DefaultWriteables
import play.api.libs.concurrent._
import db.QuotationCollectionProxy.default._
import models.Quotation.QuotationJSONWriter


object Application extends Controller with DefaultWriteables {

  val streamingActor = Akka.system.actorOf(Props[TwitterStreamingActor], name = "streamingActor")
  Akka.system.scheduler.schedule(1 hour, 1 hour) { keep(1000) } // prevent the database from growing too big

  def index = Action {
    Ok(views.html.index())
  }

  def quotation = WebSocket.using[JsValue] { request =>

    implicit val timeout = Timeout(1 seconds)

    val in = Iteratee.getChunks[JsValue]

    val out: Enumerator[JsValue] = Enumerator.repeatM {
      val p = scala.concurrent.Promise[Option[Quotation]]()
      Akka.system.scheduler.scheduleOnce(5 seconds) {
        val lastQuotation = (streamingActor ? LastQuotation).mapTo[Option[Quotation]]
        p.completeWith(lastQuotation)
      }
      p.future map (toJson(_))
    }

    (in, out)
  }

  def lastQuotation = Action.async {
    implicit val timeout = Timeout(1 seconds)
    val lastQuotationFut = (streamingActor ? LastQuotation).mapTo[Option[Quotation]]
    lastQuotationFut map {q => Ok(toJson(q)) }
  }

}