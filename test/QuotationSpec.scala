import models.{Author, Quotation}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.Quotation.QuotationJSONFormat
import play.api.libs.json.Json.toJson
import play.api.libs.json._

class QuotationSpec extends Specification with Mockito{

  "Quotation" should {

    "be serialized into json value" in {

      val createdAt = mock[java.util.Date]
      createdAt.toString returns "date"
      val location = mock[twitter4j.GeoLocation]

      val quotation = Quotation(
          None,
          "quotation",
          Author("author"),
          "@user",
          "full status",
          createdAt,
          Some(location))

      val json = toJson(quotation)
      json \ "text" must beEqualTo(JsString("quotation"))
      json \ "author" must beEqualTo(JsString("author"))
      json \ "user" must beEqualTo(JsString("@user"))
      json \ "tweetStatus" must beEqualTo(JsString("full status"))
      json \ "date" must beEqualTo(JsString("date"))
    }

  }

}
