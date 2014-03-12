import java.util.Date
import org.specs2.mutable.Specification
import models.{Author, QuotationExtractor}
import org.specs2.mock.Mockito

class QuotationExtractorSpec extends Specification with Mockito {

  def mockUser = {
    val user = mock[twitter4j.User]
    user.getName returns "Foo Bar"
    user.getScreenName returns "@foo"
    user.getMiniProfileImageURL returns "url"
  }

  def mockStatus(text: String) = {
    val status = mock[twitter4j.Status]
    status.getCreatedAt returns new Date(23233)
    status.getUser returns mockUser
    status.getText returns text
    status
  }

  "QuotationExtractor" should {

    "extract quotation from status" in {
      val status = mockStatus(
        "@someone: \"Try not to be a man of success but...\" - Albert Einstein")
      val q = QuotationExtractor(status)
      q must beSome
      q.get.author must beEqualTo(Author("Albert Einstein"))
      q.get.quote must beEqualTo("Try not to be a man of success but...")
    }

    "return None if @ present" in {
      val status = mockStatus(
        "@someone: \"@ sdsdsdsqdsqdsqdsqdqsddsddsd\" - Albert Einstein")
      val q = QuotationExtractor(status)
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
