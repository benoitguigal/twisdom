package models


import play.api.libs.json.{JsString, JsObject, JsValue, Writes}
import reactivemongo.bson._
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import reactivemongo.bson.BSONString


/**
 * A simplified Twitter user profile
 */
case class SimpleUser(
    name: String,
    screenName: String,
    imageUrl: String,
    id: Long)

object SimpleUser {

  def apply(user: twitter4j.User): SimpleUser =
    SimpleUser(user.getName, user.getScreenName, user.getProfileImageURL, user.getId)

  implicit object SimpleUserBSONWriter extends BSONDocumentWriter[SimpleUser] {
    override def write(u: SimpleUser) = {
      BSONDocument(
        "userId" -> BSONLong(u.id),
        "name" -> BSONString(u.name),
        "screen_name" -> BSONString(u.screenName),
        "image_url" -> BSONString(u.imageUrl)
      )
    }
  }

  implicit object SimpleUserJSONFormat extends Writes[SimpleUser] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(u: SimpleUser) = JsObject(Seq(
        "name" -> JsString(u.name),
        "screenName" -> JsString(u.screenName),
        "imageUrl" -> JsString(u.imageUrl),
        "id" -> JsString(u.id.toString)))
  }
}
