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


object Application extends Controller {

  val streamingActor = Akka.system.actorOf(Props[TwitterStreamingActor], name = "streamingActor")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tweets = Action.async {
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    (streamingActor ? "lastTweet").mapTo[Option[String]].map { lastTweet =>
      Ok(lastTweet.getOrElse(""))
    }
  }

}