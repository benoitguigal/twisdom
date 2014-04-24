import java.util.Date
import models.{SimpleUser, SimpleStatus}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.libs.json.Json.toJson
import play.api.libs.json.JsString

class SimpleStatusSpec extends Specification with Mockito {

  "SimpleStatusSpec" should {

    "be parsed from twitter4j Status" in {
      val t4jStatus = mock[twitter4j.Status]
      t4jStatus.getText returns "text"
      val t4ju = mock[twitter4j.User]
      t4ju.getName returns "Foo Bar"
      t4ju.getScreenName returns "@foo"
      t4ju.getProfileImageURL returns "url"
      t4ju.getId returns 1L
      t4jStatus.getUser returns t4ju
      val date = mock[Date]
      t4jStatus.getCreatedAt returns date
      val status = SimpleStatus(t4jStatus)

      status.text must beEqualTo("text")
      status.createdAt must beEqualTo(date)
    }

    "be serialized into json" in {
      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val status = SimpleStatus("text", user, new Date(604450800000L))
      val json = toJson(status)
      json \ "text" must beEqualTo(JsString("text"))
      json \ "createdAt" must beEqualTo(JsString("1989-02-25 23:00:00 UTC"))
      json \ "user" must beEqualTo(toJson(user))
    }

    "print createdAt" in {
      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val status = SimpleStatus("text", user, new Date(604450800000L))
      status.createdAtStr must beEqualTo("1989-02-25 23:00:00 UTC")
    }

  }

}
