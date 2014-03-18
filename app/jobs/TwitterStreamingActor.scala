package jobs

import play.api.Play
import akka.actor.{ActorLogging, Actor}
import twitter4j._
import models.{Quotation, QuotationExtractor, UnknownAuthor, Author}
import db.QuotationCollectionProxy.default._


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


  import Quotation.QuotationBSONWriter
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  var lastQuotation: Option[Quotation] = None

  def simpleStatusListener = new StatusListener {
    def onStallWarning(warning: StallWarning) = {}
    def onException(ex: Exception) = { ex.printStackTrace() }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
    def onScrubGeo(userId: Long, upToStatusId: Long) = {}
    def onStatus(status: Status) = {
      QuotationExtractor(status) map { q =>
        if (q.author != UnknownAuthor) { lastQuotation = Some(q) }
        updateOrInsert(q)
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
    case LastQuotation => sender ! lastQuotation
    case message => log info (s"received unknown message $message")
  }
}

case object LastQuotation



