package models


object QuotationExtractor {

  private[this] val QuotationRE = ".*\"([^@#/\"]{20,})\".*".r

  def apply(status: twitter4j.Status): Option[Quotation] = {

    lazy val author = {
      val foundInList = Author.defaults.find(a => status.getText.containsCaseInsensitive(a.name))
      foundInList.getOrElse(UnknownAuthor)
    }

    status.getText match {
      case QuotationRE(q) if (!q.contains(author.name)) =>
        Some(Quotation(None, q, author, Seq(SimpleStatus(status))))
      case _ => None
    }

  }


}
