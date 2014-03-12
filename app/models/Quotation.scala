package models

import reactivemongo.bson._
import play.api.libs.json._
import play.api.libs.json.Json.toJson

object Quotation {

  implicit object QuotationBSONReader extends BSONDocumentReader[Quotation] {

    import SimpleStatus.SimpleStatusBSONReader

    def read(document: BSONDocument) = {
      val id = document.getAs[BSONObjectID]("_id")
      val quote = document.getAs[BSONString]("quote").get.value
      val author = {
        val name = document.getAs[BSONString]("author").get.value
        Author.findByName(name).get
      }
      val statuses = document.getAs[Seq[BSONDocument]]("statuses").get.map { d =>
        BSON.readDocument[SimpleStatus](d)
      }
      Quotation(id, quote, author, statuses)
    }
  }

  implicit object QuotationBSONWriter extends BSONDocumentWriter[Quotation] {

    def write(quotation: Quotation) = {
      BSONDocument(
        "_id" -> quotation.id.getOrElse(BSONObjectID.generate),
        "quote" -> BSONString(quotation.quote),
        "author" -> BSONString(quotation.author.name),
        "statuses" -> quotation.statuses.map(BSON.writeDocument(_)))
    }
  }

  implicit object QuotationJSONFormat extends Format[Quotation] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(q: Quotation) = JsObject(Seq(
      "quote" -> JsString(q.quote),
      "author" -> JsString(q.author.displayableName),
      "status" -> toJson(q.statuses)))
  }
}


case class Quotation(
    id: Option[BSONObjectID],
    quote: String,
    author: Author,
    statuses: Seq[SimpleStatus])


