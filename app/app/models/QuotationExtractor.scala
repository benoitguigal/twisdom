package models


class QuotationExtractor {

  lazy private[this] val QuotationRE = ".*\"([^@#/\"]{20,})\".*".r
  lazy protected val languageDetector = new LanguageDetector

  def apply(status: SimpleStatus): Option[Quotation] = {

    def author = {
      val foundInList = Author.defaults.find(a => status.text.containsCaseInsensitive(a.name))
      foundInList.getOrElse(UnknownAuthor)
    }

    def lang = languageDetector(status.text)

    status.text match {
      case QuotationRE(q) if (!q.contains(author.name)) =>
        Some(Quotation(q, author, lang))
      case _ => None
    }
  }
}
