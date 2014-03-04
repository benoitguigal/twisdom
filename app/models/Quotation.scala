package models

import reactivemongo.bson._
import play.api.libs.json._
import play.api.libs.json.Json.toJson

object Quotation {

  implicit object QuotationBSONReader extends BSONDocumentReader[Quotation] {

    import SimpleStatus.SimpleStatusBSONReader

    def read(document: BSONDocument) = {
      val id = document.getAs[BSONObjectID]("_id")
      val text = document.getAs[BSONString]("text").get.value
      val author = {
        val name = document.getAs[BSONString]("author").get.value
        Author.findByName(name).get
      }
      val status = BSON.readDocument[SimpleStatus](document.getAs[BSONDocument]("user").get)
      Quotation(id, text, author, status)
    }
  }

  implicit object QuotationBSONWriter extends BSONDocumentWriter[Quotation] {

    import SimpleStatus.SimpleStatusWriter

    def write(quotation: Quotation) = {
      BSONDocument(
        "_id" -> quotation.id.getOrElse(BSONObjectID.generate),
        "text" -> BSONString(quotation.quotes),
        "author" -> BSONString(quotation.author.name),
        "status" -> BSON.write(quotation.status))
    }
  }

  implicit object QuotationJSONFormat extends Format[Quotation] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(q: Quotation) = JsObject(Seq(
      "text" -> JsString(q.quotes),
      "author" -> JsString(q.author.displayableName),
      "status" -> toJson(q.status)))
  }
}


case class Quotation(
    id: Option[BSONObjectID],
    quotes: String,
    author: Author,
    status: SimpleStatus)


