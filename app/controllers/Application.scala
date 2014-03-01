package controllers

import play.api.mvc._
import play.api.libs.concurrent.Akka
import jobs.{GetLastQuotation, UpdateQuotation, LastQuotationActor, TwitterStreamingActor}
import akka.actor.Props
import akka.pattern.ask
import play.api.Play.current
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits._
import models.Quotation
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, JsNull}
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.http.DefaultWriteables



object Application extends Controller with DefaultWriteables {

  val streamingActor = Akka.system.actorOf(Props[TwitterStreamingActor], name = "streamingActor")
  val lastQuotationActor = Akka.system.actorOf(Props[LastQuotationActor], name = "lastQuotationActor")
  Akka.system.scheduler.schedule(0 seconds, 15 seconds) {
    lastQuotationActor ! UpdateQuotation
  }


  def index = Action {
    Ok(views.html.index())
  }


  lazy val quotations: Enumerator[String] = {
    implicit val timeout = Timeout(100 milliseconds)
    Enumerator.repeatM[String] {
      (lastQuotationActor ? GetLastQuotation).mapTo[Option[Quotation]] map {
        case None => ""
        case Some(q) => q.text //toJson(q)
      }
    }
  }


  def quotation = WebSocket.using[JsValue] { request =>
    implicit val timeout = Timeout(100 milliseconds)
    val in = Iteratee.foreach[JsValue](println).map { _ =>
      println("Disconnected")
    }

    val out = Enumerator.repeatM[JsValue] {
      (lastQuotationActor ? GetLastQuotation).mapTo[Option[Quotation]] map {
        case None => JsNull
        case Some(q) => toJson(q)
      }
    }
    (in, out)
  }


}