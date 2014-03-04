package models

import reactivemongo.bson._
import reactivemongo.bson.BSONString
import play.api.libs.json.{JsString, JsObject, JsValue, Format}
import play.api.libs.json.Json.toJson


/**
 * A simplified Twitter user status
 */

case class SimpleStatus(
    text: String,
    user: SimpleUser,
    createdAt: java.util.Date)

object SimpleStatus {

  def apply(status: twitter4j.Status): SimpleStatus =
    SimpleStatus(status.getText, SimpleUser(status.getUser), status.getCreatedAt)

  implicit object SimpleStatusBSONReader extends BSONDocumentReader[SimpleStatus] {

    import SimpleUser.SimpleUserBSONReader
    def read(document: BSONDocument) = {
      val text = document.getAs[BSONString]("text").get.value
      val user = BSON.readDocument[SimpleUser](document.getAs[BSONDocument]("user").get)
      val createdAt = document.getAs[BSONDateTime]("createdAt").get.value
      SimpleStatus(text, user, new java.util.Date(createdAt))
    }
  }

  implicit object SimpleStatusWriter extends BSONDocumentWriter[SimpleStatus] {
    def write(s: SimpleStatus) = {
      BSONDocument(
          "text" -> BSONString(s.text),
          "user" -> BSON.write(s.user),
          "createdAt" -> BSONDateTime(s.createdAt.getTime))
    }
  }

  implicit object SimpleStatusJSONFormat extends Format[SimpleStatus] {

    def writes(s: SimpleStatus) = JsObject(Seq(
        "text" -> JsString(s.text),
        "user" -> toJson(s.user),
        "createdAt" -> JsString(s.createdAt.toString)
    ))

    def reads(json: JsValue) = throw new Exception("not implemented")
  }
}


