package controllers

import play.api.mvc._
import play.api.libs.concurrent.Akka
import jobs.TwitterStreamingActor
import akka.actor.Props
import akka.pattern.ask
import play.api.Play.current
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import play.api.libs.concurrent.Execution.Implicits._
import models.Quotation
import play.api.libs.json.Json.toJson




object Application extends Controller {

  val streamingActor = Akka.system.actorOf(Props[TwitterStreamingActor], name = "streamingActor")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def quotation = Action.async {
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    (streamingActor ? "lastQuotation").mapTo[Option[Quotation]].map { lastQuotation =>
      lastQuotation match {
        case None => Ok("No quotation to be displayed")
        case Some(q) => Ok(toJson(q))
      }
    }
  }

}