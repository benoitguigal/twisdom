package jobs

import akka.actor.Actor
import play.api.Play
import twitter4j._
import models.Author
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}


class TwitterStreamListener extends Actor with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  def receive = {
    case status: Status => context.parent ! status
  }

  // create a TwitterStream and add listener
  val twitterStream = new TwitterStreamFactory(TwitterConfig()).getInstance
  twitterStream.addListener(simpleStatusListener)

  // add  author filter to the stream
  val authorFilter = new FilterQuery()
  authorFilter.track(Author.defaults.map(_.name).toArray)
  twitterStream.filter(authorFilter)

  def simpleStatusListener = new StatusListener {
    def onStallWarning(warning: StallWarning) = {}
    def onException(ex: Exception) = { ex.printStackTrace() }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
    def onScrubGeo(userId: Long, upToStatusId: Long) = {}
    def onStatus(status: Status) = {
      context.self ! status
    }
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) = {}
  }

  override def postStop() = {
    twitterStream.shutdown()
    super.postStop()
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
