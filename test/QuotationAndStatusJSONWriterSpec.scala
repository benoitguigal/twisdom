import java.util.Date
import models.{SimpleUser, SimpleStatus, Author, Quotation}
import org.specs2.mutable.Specification
import play.api.libs.json.Json.toJson
import play.api.libs.json.Json.prettyPrint
import models.QuotationAndStatusJSONWriter

class QuotationAndStatusJSONWriterSpec extends Specification {

  "QuotationAndStatusWiterJSONWriter" should {

    "serialize a (Quotation, SimpleStatus) into json" in {

      val quotation = Quotation("quote", Author("author"))
      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val status = SimpleStatus("text", user, new Date(604450800000L))
      val serialized = prettyPrint(toJson((quotation, status)))
      serialized must beEqualTo(
      """{
        |  "quote" : "quote",
        |  "author" : "author",
        |  "status" : {
        |    "text" : "text",
        |    "user" : {
        |      "name" : "Foo Bar",
        |      "screenName" : "@foo",
        |      "imageUrl" : "imageurl",
        |      "id" : "1"
        |    },
        |    "createdAt" : "1989-02-25 23:00:00 UTC"
        |  }
        |}""".stripMargin
      )
    }

  }

}
