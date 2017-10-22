import play.api.libs.json.{Writes, JsObject}
import play.api.libs.json.Json.toJson


package object models {

  implicit class CaseInsensitiveString(s: String)  {
    def containsCaseInsensitive(other: String) = s.toLowerCase.contains(other.toLowerCase())
  }

  import Quotation.QuotationJSONWriter
  import SimpleStatus.SimpleStatusJSONWriter

  implicit object QuotationAndStatusJSONWriter extends Writes[(Quotation, SimpleStatus)] {
    override def writes(o: (Quotation, SimpleStatus)) = o match {
      case (q, s) =>
        toJson(q).asInstanceOf[JsObject] + ("status" -> toJson(s))
    }
  }

}
