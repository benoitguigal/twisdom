package models

import reactivemongo.bson._
import play.api.libs.json._
import java.util.Date

object Quotation {

  implicit object QuotationBSONReader extends BSONDocumentReader[Quotation] {

    def read(document: BSONDocument) = {
      val quote = document.getAs[BSONString]("quote").get.value
      val author = {
        val name = document.getAs[BSONString]("author").get.value
        Author.findByName(name).get
      }
      val lang = document.getAs[BSONString]("lang").get.value
      Quotation(quote, author, lang)
    }
  }

  implicit object QuotationBSONWriter extends BSONDocumentWriter[Quotation] {

    def write(quotation: Quotation) = {
      BSONDocument(
        "quote" -> BSONString(quotation.quote),
        "author" -> BSONString(quotation.author.name),
        "lang" -> BSONString(quotation.lang),
        "timestamp" -> BSONDateTime(new Date().getTime))
    }
  }

  implicit object QuotationJSONWriter extends Writes[Quotation] {

    def writes(q: Quotation) = JsObject(Seq(
      "quote" -> JsString(q.quote),
      "author" -> JsString(q.author.displayableName)))
  }
}


case class Quotation(quote: String, author: Author, lang: String)


