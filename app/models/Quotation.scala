package models

import java.util.Date
import reactivemongo.bson._
import twitter4j.GeoLocation
import play.api.libs.json._

object Quotation {

  implicit object QuotationBSONReader extends BSONDocumentReader[Quotation] {

    def read(document: BSONDocument) = {
      val id = document.getAs[BSONObjectID]("_id")
      val text = document.getAs[BSONString]("text").get.value
      val author = {
        val name = document.getAs[BSONString]("author").get.value
        Author.findByName(name).get
      }
      val twitterUser = document.getAs[BSONString]("twitterUser").get.value
      val tweetStatus = document.getAs[BSONString]("tweetStatus").get.value
      val tweetCreatedAt = {
        val millis = document.getAs[BSONDateTime]("tweetCreatedAt").get.value
        new Date(millis)
      }
      val geoLocation = {
        val latitude = document.getAs[BSONDouble]("latitude")
        val longitude = document.getAs[BSONDouble]("longitude")
        (latitude, longitude) match {
          case (Some(lat), Some(long)) => Some(new GeoLocation(lat.value, long.value))
          case _ => None
        }
      }
      Quotation(id, text, author, twitterUser, tweetStatus, tweetCreatedAt, geoLocation)
    }
  }

  implicit object QuotationBSONWriter extends BSONDocumentWriter[Quotation] {

    def write(quotation: Quotation) = {
      val bson = BSONDocument(
        "_id" -> quotation.id.getOrElse(BSONObjectID.generate),
        "text" -> BSONString(quotation.text),
        "author" -> BSONString(quotation.author.name),
        "twitterUser" -> BSONString(quotation.twitterUser),
        "tweetStatus" -> BSONString(quotation.tweetStatus),
        "tweetCreatedAt" -> BSONDateTime(quotation.tweetCreatedAt.getTime))
      val geoLocationOpt = quotation.geoLocation
      if (geoLocationOpt.isDefined) {
        val geoLoc = geoLocationOpt.get
        bson ++
          BSONDocument(
            "latitude" -> BSONDouble(geoLoc.getLatitude()),
            "longitude" -> BSONDouble(geoLoc.getLongitude()))
      }
      bson
    }
  }

  implicit object QuotationJSONFormat extends Format[Quotation] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(q: Quotation) = JsObject(Seq(
      "text" -> JsString(q.text),
      "author" -> JsString(q.author.displayableName),
      "user" -> JsString(q.twitterUser),
      "tweetStatus" -> JsString(q.tweetStatus)))
  }

}


case class Quotation(
    id: Option[BSONObjectID],
    text: String,
    author: Author,
    twitterUser: String,
    tweetStatus: String,
    tweetCreatedAt: Date,
    geoLocation: Option[GeoLocation])


