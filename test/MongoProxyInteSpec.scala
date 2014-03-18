import db.MongoProxy
import java.util.Date
import models.{SimpleStatus, SimpleUser, Author, Quotation}
import org.specs2.mutable.{Before, Specification}
import reactivemongo.api.MongoDriver
import reactivemongo.bson.BSONDocument
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Requires a MongoDB server running on localhost
 */
class MongoProxyInteSpec extends Specification {

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = connection("mydb")
  val proxy = new MongoProxy(db)

  //make sure the collection is empty
  Await.result(proxy.flush(), Duration(2, SECONDS))

  "MongoProxy" should {

    "update of insert a quotation" in {
      val user = SimpleUser("Foo Bar", "@foo", "imageurl", 1L)
      val status = SimpleStatus("text1", user, new Date(604450800000L))
      val quotation = Quotation(None, "quote", Author("Albert Einstein"), Seq(status))

      // insert because quotation is not present
      Await.result(proxy.updateOrInsert(quotation), Duration(2, SECONDS))
      val quotations = Await.result(
          proxy.quotationsCollection.find(BSONDocument()).cursor[Quotation].collect[List](15),
          Duration(2, SECONDS))
      quotations must haveSize(1)
      quotations.head.statuses must have size (1)

      // update because quotation is already present
      Await.result(proxy.updateOrInsert(quotation), Duration(2, SECONDS))
      val quotations2 = Await.result(
        proxy.quotationsCollection.find(BSONDocument()).cursor[Quotation].collect[List](15),
        Duration(2, SECONDS))
      quotations2 must haveSize(1)
      quotations2.head.statuses must have size (2)
    }

  }

}
