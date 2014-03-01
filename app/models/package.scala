

package object models {

  implicit class CaseInsensitiveString(s: String)  {
    def containsCaseInsensitive(other: String) = s.toLowerCase.contains(other.toLowerCase())
  }

}
