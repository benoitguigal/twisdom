package models

class QuotationExtractor {

  private[this] val QuotationRE = ".*\"([^@#/\"]{20,})\".*".r

  def apply(status: SimpleStatus): Option[Quotation] = {

    lazy val author = {
      val foundInList = Author.defaults.find(a => status.text.containsCaseInsensitive(a.name))
      foundInList.getOrElse(UnknownAuthor)
    }

    status.text match {
      case QuotationRE(q) if (!q.contains(author.name)) =>
        Some(Quotation(q, author))
      case _ => None
    }
  }
}
