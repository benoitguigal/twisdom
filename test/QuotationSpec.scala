
import models.{Author, Quotation}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import reactivemongo.bson.BSON

class QuotationSpec extends Specification with Mockito {

  "Quotation" should {


    "be serialized into json value" in {
      pending
    }

    "be serialized in bson and deserialized" in {

      import Quotation.{QuotationBSONReader, QuotationBSONWriter}

      val quotation = Quotation("quote", Author("Albert Einstein"))
      val serialized = BSON.write(quotation)
      val deserialized = BSON.read(serialized)
      deserialized.author must beEqualTo(quotation.author)
      deserialized.quote must beEqualTo(quotation.quote)
    }

  }

}
