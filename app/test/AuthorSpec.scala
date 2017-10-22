import models.Author
import org.specs2.mutable.Specification


class AuthorSpec extends Specification {

  "Author" should {

    "find an author by its name" in {
      Author.findByName("Aesop") must beSome(Author("Aesop"))
    }

    "return None if not author with such name was found" in {
      Author.findByName("unknown") must beNone
    }

    "find an author in a tweet status" in {
      val status = "Imagination is more important than knowledge - Albert Einstein"
      Author.findInStatus(status) must beSome(Author("Albert Einstein"))
    }

  }

}
