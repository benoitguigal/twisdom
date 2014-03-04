import models.{Author, Quotation}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.Quotation.QuotationJSONFormat
import play.api.libs.json.Json.toJson
import play.api.libs.json._

class QuotationSpec extends Specification with Mockito{

  "Quotation" should {

    "be serialized into json value" in {
      pending
    }

  }

}
