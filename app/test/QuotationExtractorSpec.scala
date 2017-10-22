import java.util.Date
import org.specs2.mutable.Specification
import models._
import org.specs2.mock.Mockito

class QuotationExtractorSpec extends Specification with Mockito {


  def mockStatus(text: String) = {
    val status = mock[SimpleStatus]
    status.createdAt returns new Date(23233)
    status.user returns mock[SimpleUser]
    status.text returns text
    status
  }

  def mockLanguageDetector = {
    val detector = mock[LanguageDetector]
    detector.apply(anyString) returns "en"
    detector
  }

  val extractor = new QuotationExtractor {
    override protected lazy val languageDetector = mockLanguageDetector
  }

  "QuotationExtractor" should {

    "extract quotation from status" in {
      val status = mockStatus(
        "@someone: \"Try not to be a man of success but...\" - Albert Einstein")
      val q = extractor(status)
      q must beSome
      q.get.author must beEqualTo(Author("Albert Einstein"))
      q.get.quote must beEqualTo("Try not to be a man of success but...")
      q.get.lang must beEqualTo("en")
    }

    "return None if @ present" in {
      val status = mockStatus(
        "@someone: \"@ sdsdsdsqdsqdsqdsqdqsddsddsd\" - Albert Einstein")
      val q = extractor(status)
      q must beNone
    }

    "return None if # present" in {
      pending
    }

    "return None if / present" in {
      pending
    }

    "return None if shorter than 20 char" in {
      pending
    }

    "return None if author name present inside the quotes" in {
      pending
    }

  }


}
