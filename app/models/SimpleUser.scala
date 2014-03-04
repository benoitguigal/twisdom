package models

import reactivemongo.bson.{BSONDocumentWriter, BSONString, BSONDocument, BSONDocumentReader}
import play.api.libs.json.{JsString, JsObject, JsValue, Format}

/**
 * A simplified Twitter user profile
 */
case class SimpleUser(
    name: String,
    screenName: String,
    imageUrl: String)

object SimpleUser {

  def apply(user: twitter4j.User): SimpleUser =
    SimpleUser(user.getName, user.getScreenName, user.getMiniProfileImageURL)

  implicit object SimpleUserBSONReader extends BSONDocumentReader[SimpleUser] {

    def read(document: BSONDocument) = {
      val name = document.getAs[BSONString]("name").get.value
      val screenName = document.getAs[BSONString]("screenName").get.value
      val imageUrl = document.getAs[BSONString]("imageUrl").get.value
      SimpleUser(name, screenName, imageUrl)
    }
  }

  implicit object SimpleUserWriter extends BSONDocumentWriter[SimpleUser] {

    def write(u: SimpleUser) = {
      BSONDocument(
        "name" -> BSONString(u.name),
        "screenName" -> BSONString(u.screenName),
        "imageUrl" -> BSONString(u.imageUrl))
    }
  }

  implicit object SimpleUserJSONFormat extends Format[SimpleUser] {

    def reads(json: JsValue) = throw new Exception("not implemented")

    def writes(u: SimpleUser) = JsObject(Seq(
        "name" -> JsString(u.name),
        "screenName" -> JsString(u.screenName),
        "imageUrl" -> JsString(u.imageUrl)))
  }
}
