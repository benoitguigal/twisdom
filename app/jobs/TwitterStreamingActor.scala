package jobs

import play.api.Play
import akka.actor.{ActorLogging, Actor}
import twitter4j._
import models.{Quotation, QuotationExtractor, UnknownAuthor, Author}
import db.MongoProxy


object TwitterConfig {

  private[this] def consumerKey     = Play.current.configuration.getString("twitter.consumerKey").get
  private[this] def consumerSecret  = Play.current.configuration.getString("twitter.consumerSecret").get
  private[this] def token           = Play.current.configuration.getString("twitter.token").get
  private[this] def tokenSecret     = Play.current.configuration.getString("twitter.tokenSecret").get

  def apply() = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(token)
    .setOAuthAccessTokenSecret(tokenSecret)
    .build()

}

class TwitterStreamingActor extends Actor with ActorLogging {

  var lastQuotation: Option[Quotation] = None

  import Quotation.QuotationBSONWriter
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  def simpleStatusListener = new StatusListener {
    def onStallWarning(warning: StallWarning) = {}
    def onException(ex: Exception) = { ex.printStackTrace() }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
    def onScrubGeo(userId: Long, upToStatusId: Long) = {}
    def onStatus(status: Status) = {
      log.info(s"Got status ${status.getText}")
      QuotationExtractor(status) map { q =>
        MongoProxy.quotationsCollection.insert(q)
        q.author match {
          case UnknownAuthor =>
          case author => { lastQuotation = Some(q) }
        }
      }
    }
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) = {}
  }

  val twitterStream = new TwitterStreamFactory(TwitterConfig()).getInstance
  twitterStream.addListener(simpleStatusListener)

  val authorFilter = new FilterQuery()
  authorFilter.track(Author.defaults.map(_.name).toArray)

  twitterStream.filter(authorFilter)

  def receive = {
    case "lastQuotation" => sender ! lastQuotation
    case _ => log info ("received unknown message")
  }



}
