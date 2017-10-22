package jobs

import akka.actor.ActorRef
import play.api.Play
import twitter4j._
import models.Author


class TwitterStream {

  // create a TwitterStream and add listener
  private val twitterStream = new TwitterStreamFactory(TwitterConfig()).getInstance
  twitterStream.addListener(simpleStatusListener)

  // add  author filter to the stream
  val authorFilter = new FilterQuery()
  authorFilter.track(Author.defaults.map(_.name).toArray)
  twitterStream.filter(authorFilter)

  private var consumers = Seq.empty[ActorRef]

  def shutdown() = twitterStream.shutdown()

  def register(consumer: ActorRef) = {
    consumers = consumers :+ consumer
  }

  def simpleStatusListener = new StatusListener {
    def onStallWarning(warning: StallWarning) = {}
    def onException(ex: Exception) = { ex.printStackTrace() }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
    def onScrubGeo(userId: Long, upToStatusId: Long) = {}
    def onStatus(status: Status) = {
      consumers foreach (_ ! status)
    }
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) = {}
  }

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

}
