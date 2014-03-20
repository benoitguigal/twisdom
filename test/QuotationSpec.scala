import java.util.Date
import models.{Author, Quotation}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import reactivemongo.bson.BSON

class QuotationSpec extends Specification with Mockito {

  "Quotation" should {

    "calculate popularity" in {
      val q =  Quotation(None, "quote", Author("Albert Einstein"), Seq(new Date(0L), new Date(1L)))
      q.popularity must beEqualTo(2)
    }

    "merge with another quotation" in {
      val q1 = Quotation(None, "quote", Author("Albert Einstein"), Seq(new Date(0L), new Date(1L)))
      val q2 = Quotation(None, "quote",  Author("Albert Einstein"), Seq(new Date(2L)))
      val q3 = q1.merge(q2)
      q3 must beEqualTo(
        Quotation(None, "quote", Author("Albert Einstein"), Seq(new Date(0L), new Date(1L), new Date(2L))))
      q3.popularity must beEqualTo(3)
    }


    "be serialized into json value" in {
      pending
    }

    "be serialized in bson and deserialized" in {

      import Quotation.{QuotationBSONReader, QuotationBSONWriter}

      val shares = Seq(new Date(604450800000L), new Date(604450800001L))
      val quotation = Quotation(None, "quote", Author("Albert Einstein"), shares)
      val serialized = BSON.write(quotation)
      val deserialized = BSON.read(serialized)
      deserialized.author must beEqualTo(quotation.author)
      deserialized.quote must beEqualTo(quotation.quote)
      deserialized.shares must beEqualTo(quotation.shares)
      deserialized.popularity must beEqualTo(quotation.popularity)
    }

  }

}
