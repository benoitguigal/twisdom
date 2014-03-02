import models.CaseInsensitiveString
import org.specs2.mutable._


class CaseInsensitiveStringSpec extends Specification {

  "CaseInsensitiveString" should {

    "perform a case insensitive contains" in {
      val caseInsensitive = new CaseInsensitiveString("I am A CaSe SenSItive sTRING")
      val other = "case SENSITIVE sTring"
      caseInsensitive.containsCaseInsensitive(other) must beTrue
    }
  }

}
