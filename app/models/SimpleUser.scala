package models

import reactivemongo.bson._
import play.api.libs.json.{JsString, JsObject, JsValue, Format}
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

  implicit object SimpleUserBSONReader extends BSONDocumentReader[SimpleUser] {

    def read(document: BSONDocument) = {
      val name = document.getAs[BSONString]("name").get.value
      val screenName = document.getAs[BSONString]("screenName").get.value
      val imageUrl = document.getAs[BSONString]("imageUrl").get.value
      val id = document.getAs[BSONLong]("id").get.value
      SimpleUser(name, screenName, imageUrl, id)
    }
  }

  implicit object SimpleUserWriter extends BSONDocumentWriter[SimpleUser] {

    def write(u: SimpleUser) = {
      BSONDocument(
        "name" -> BSONString(u.name),
        "screenName" -> BSONString(u.screenName),
        "imageUrl" -> BSONString(u.imageUrl),
        "id" -> BSONLong(u.id))
    }
  }

  implicit object SimpleUserJSONFormat extends Format[SimpleUser] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(u: SimpleUser) = JsObject(Seq(
        "name" -> JsString(u.name),
        "screenName" -> JsString(u.screenName),
        "imageUrl" -> JsString(u.imageUrl),
        "id" -> JsString(u.id.toString)))
  }
}
