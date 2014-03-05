import models.SimpleUser
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsString

class SimpleUserSpec extends Specification with Mockito {

  "SimpleUser" should {

    "be parsed from twitter4j.User" in {
      val t4ju = mock[twitter4j.User]
      t4ju.getName returns "Foo Bar"
      t4ju.getScreenName returns "@foo"
      t4ju.getProfileImageURL returns "imageurl"
      t4ju.getId returns 1L
      val user = SimpleUser(t4ju)
      user.name must beEqualTo("Foo Bar")
      user.screenName must beEqualTo("@foo")
      user.imageUrl must beEqualTo("imageurl")
      user.id must beEqualTo(1L)
    }

    "serialize into json" in {
      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val json = toJson(user)
      json \ "name" must beEqualTo(JsString("Foo Bar"))
      json \ "screenName" must beEqualTo(JsString("@foo"))
      json \ "imageUrl" must beEqualTo(JsString("imageurl"))
      json \ "id" must beEqualTo(JsString("1"))
    }

  }
}
