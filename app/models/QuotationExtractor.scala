package models


object QuotationExtractor {

  private[this] val QuotationRE = ".*\"(.+)\".*".r

  def apply(status: twitter4j.Status): Option[Quotation] = {
    val rawText = status.getText
    val textOpt = rawText match {
      case QuotationRE(q) => Some(q)
      case _ => None
    }
    textOpt map { text =>
      val twitterUser = status.getUser
      val author = Author.defaults.find(a => rawText.containsCaseInsensitive(a.name))
      val tweetCreatedAt = status.getCreatedAt
      val location = status.getGeoLocation match {
        case null => None
        case geoLoc => Some(geoLoc)
      }
      Quotation(
        None,
        text,
        author.getOrElse(UnknownAuthor),
        twitterUser.getName,
        rawText,
        tweetCreatedAt,
        location)
    }
  }

}
