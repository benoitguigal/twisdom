package models

import play.api.libs.json.{JsString, JsObject, JsValue, Writes}
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


  implicit object SimpleStatusJSONWriter extends Writes[SimpleStatus] {

    def writes(s: SimpleStatus) = JsObject(Seq(
        "text" -> JsString(s.text),
        "user" -> toJson(s.user),
        "createdAt" -> JsString(s.createdAt.toString)
    ))

    def reads(json: JsValue) = throw new Exception("not implemented")
  }
}


