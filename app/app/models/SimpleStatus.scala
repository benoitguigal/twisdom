package models

import play.api.libs.json.{JsString, JsObject, JsValue, Writes}
import play.api.libs.json.Json.toJson
import java.text.SimpleDateFormat
import SimpleStatus.dateFormat
import java.util.TimeZone


/**
 * A simplified Twitter user status
 */

case class SimpleStatus(
    text: String,
    user: SimpleUser,
    createdAt: java.util.Date) {

  lazy val createdAtStr = dateFormat.format(createdAt)

}

object SimpleStatus {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
  dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

  def apply(status: twitter4j.Status): SimpleStatus =
    SimpleStatus(status.getText, SimpleUser(status.getUser), status.getCreatedAt)


  implicit object SimpleStatusJSONWriter extends Writes[SimpleStatus] {

    def writes(s: SimpleStatus) = JsObject(Seq(
        "text" -> JsString(s.text),
        "user" -> toJson(s.user),
        "createdAt" -> JsString(s.createdAtStr)
    ))

    def reads(json: JsValue) = throw new Exception("not implemented")
  }
}


