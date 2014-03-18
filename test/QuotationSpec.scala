import java.util.Date
import models.{Author, Quotation, SimpleUser, SimpleStatus}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import reactivemongo.bson.BSON

class QuotationSpec extends Specification with Mockito{

  "Quotation" should {

    "be serialized into json value" in {
      pending
    }

    "be serialized in bson and deserialized" in {

      import Quotation.{QuotationBSONReader, QuotationBSONWriter}

      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val status1 = SimpleStatus("text1", user, new Date(604450800000L))
      val status2 = SimpleStatus("text2", user, new Date(604450800001L))
      val quotation = Quotation(None, "quote", Author("Albert Einstein"), Seq(status1, status2))
      val serialized = BSON.write(quotation)
      val deserialized = BSON.read(serialized)
      deserialized.author must beEqualTo(quotation.author)
      deserialized.quote must beEqualTo(quotation.quote)
      deserialized.statuses must beEqualTo(quotation.statuses)
    }

  }

}
