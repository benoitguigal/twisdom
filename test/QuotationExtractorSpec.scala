import java.util.Date
import org.specs2.mutable.Specification
import models.QuotationExtractor
import org.specs2.mock.Mockito

class QuotationExtractorSpec extends Specification with Mockito {


  "QuotationExtractor" should {

    "parse a Quotation when there is a quotation match" in {

      val status = mock[twitter4j.Status]
      status.getText returns "\"Try not to be a man of success but a man o value\" - Albert Einstein"
      val user = mock[twitter4j.User]
      user.getName returns "@BGuigal"
      status.getUser returns user
      val date = mock[Date]
      status.getCreatedAt returns date
      val location =  mock[twitter4j.GeoLocation]
      status.getGeoLocation returns location

      val quotationOpt = QuotationExtractor(status)
      quotationOpt must beSome
      val quotation = quotationOpt.get
      quotation.text must beEqualTo("Try not to be a man of success but a man o value")
      quotation.author.name must beEqualTo("Albert Einstein")
      quotation.id must beNone
      quotation.twitterUser must beEqualTo("@BGuigal")
      quotation.geoLocation must beSome(location)
      quotation.tweetCreatedAt must beEqualTo(date)
    }

    "return None when there is no quotation match" in {
      val status = mock[twitter4j.Status]
      status.getText returns "This a status with no quotation, even if there is the name Albert Einstein"
      QuotationExtractor(status) must beNone
    }

  }


}
