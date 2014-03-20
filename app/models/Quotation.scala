package models

import reactivemongo.bson._
import play.api.libs.json._
import java.util.Date

object Quotation {

  implicit object QuotationBSONReader extends BSONDocumentReader[Quotation] {

    def read(document: BSONDocument) = {
      val id = document.getAs[BSONObjectID]("_id")
      val quote = document.getAs[BSONString]("quote").get.value
      val author = {
        val name = document.getAs[BSONString]("author").get.value
        Author.findByName(name).get
      }
      val shares = document.getAs[Seq[BSONDateTime]]("shares").get.map(d => new Date(d.value))
      Quotation(id, quote, author, shares)
    }
  }

  implicit object QuotationBSONWriter extends BSONDocumentWriter[Quotation] {

    def write(quotation: Quotation) = {
      BSONDocument(
        "_id" -> quotation.id.getOrElse(BSONObjectID.generate),
        "quote" -> BSONString(quotation.quote),
        "author" -> BSONString(quotation.author.name),
        "shares" -> quotation.shares.map(d => BSONDateTime(d.getTime)),
        "popularity" -> BSONInteger(quotation.popularity))
    }
  }

  implicit object QuotationJSONWriter extends Writes[Quotation] {

    def writes(q: Quotation) = JsObject(Seq(
      "quote" -> JsString(q.quote),
      "author" -> JsString(q.author.displayableName),
      "popularity" -> JsNumber(q.popularity)))
  }

}

/**
 *
 * @param id
 * @param quote
 * @param author
 * @param shares The date when the quotation was shared
 */
case class Quotation(
    id: Option[BSONObjectID],
    quote: String,
    author: Author,
    shares: Seq[Date]) {

  lazy val popularity = shares.size

  def merge(that: Quotation) = {
    require(this.quote == that.quote)
    require(this.author == that.author)
    copy(shares = shares ++ that.shares)
  }
}


