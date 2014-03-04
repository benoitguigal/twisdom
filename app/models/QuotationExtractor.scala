package models


object QuotationExtractor {

  private[this] val QuotationRE = ".*\"(.+)\".*".r

  def apply(status: twitter4j.Status): Option[Quotation] = {
    val rawText = status.getText
    val textOpt = rawText match {
      case QuotationRE(q) if (!q.contains("#") && !q.contains("@") &&  q.length > 20) => Some(q)
      case _ => None
    }
    textOpt map { text =>
      val author = Author.defaults.find(a => rawText.containsCaseInsensitive(a.name))
      val s = SimpleStatus(status)
      Quotation(None, text, author.getOrElse(UnknownAuthor), s)
    }
  }


}
