package controllers

import play.api.mvc._
import jobs.TwitterStreamingActor
import akka.actor.Props
import play.api.Play.current
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import models.Quotation
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, JsNull}
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.http.DefaultWriteables
import play.api.libs.concurrent._
import db.MongoProxy
import models.Quotation.QuotationJSONFormat


object Application extends Controller with DefaultWriteables {

  val streamingActor = Akka.system.actorOf(Props[TwitterStreamingActor], name = "streamingActor")

  def index = Action {
    Ok(views.html.index())
  }

  def quotation = WebSocket.using[JsValue] { request =>

    implicit val timeout = Timeout(5 seconds)

    val in = Iteratee.getChunks[JsValue]

    val out: Enumerator[JsValue] = Enumerator.repeatM {
      val p = scala.concurrent.Promise[Option[Quotation]]()
      Akka.system.scheduler.scheduleOnce(5 seconds) {
        p.completeWith(MongoProxy.lastQuotation)
      }
      p.future map {
        case None => JsNull
        case Some(q) => toJson(q)
      }
    }

    (in, out)
  }


}