import models.LanguageDetector
import org.specs2.mutable.Specification


class LanguageDetectorSpec extends Specification {

  val languageDetector = new LanguageDetector

  "LanguageDetector" should {

    "detect language in a text" in {
      val lang = languageDetector("Hello there, here is a text in english")
      lang must beEqualTo ("en")
    }

  }

}
