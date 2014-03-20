import db.QuotationCollectionProxy
import java.util.Date
import models.{SimpleStatus, SimpleUser, Author, Quotation}
import org.specs2.mutable.{Specification}
import play.api.libs.iteratee.Enumerator
import reactivemongo.api.MongoDriver
import reactivemongo.bson.BSONDocument
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Requires a MongoDB server running on localhost
 */
class QuotationCollectionProxyInteSpec extends Specification {
  sequential

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = connection("mydb")
  val proxy = new QuotationCollectionProxy(db)


  "QuotationCollectionProxy" should {

    "update of insert a quotation" in {
      //make sure the collection is empty
      Await.result(proxy.flush(), Duration(2, SECONDS))
      val quotation = Quotation(None, "quote", Author("Albert Einstein"), Seq(new Date(604450800000L)))

      // insert because quotation is not present
      Await.result(proxy.updateOrInsert(quotation), Duration(2, SECONDS))
      val quotations = Await.result(
          proxy.quotationCollection.find(BSONDocument()).cursor[Quotation].collect[List](15),
          Duration(2, SECONDS))
      quotations must haveSize(1)
      quotations.head.popularity must beEqualTo(1)

      // update because quotation is already present
      Await.result(proxy.updateOrInsert(quotation), Duration(2, SECONDS))
      val quotations2 = Await.result(
        proxy.quotationCollection.find(BSONDocument()).cursor[Quotation].collect[List](15),
        Duration(2, SECONDS))
      quotations2 must haveSize(1)
      quotations2.head.popularity must beEqualTo(2)

    }

    "keep n most popular quotations" in {
      //make sure the collection is empty
      Await.result(proxy.flush(), Duration(2, SECONDS))
      val date = new Date(604450800000L)
      val quotation = Quotation(None, "quote", Author("Albert Einstein"), Seq(date))
      val quotations = (1 to 100) map (i => quotation.copy(shares = (1 to i).map(_ => date)))
      val enumerator = Enumerator.enumerate(quotations)
      Await.result(proxy.quotationCollection.bulkInsert(enumerator), Duration(30, SECONDS))
      Await.result(proxy.keep(50),Duration(5, SECONDS))
      val find = Await.result(
        proxy.quotationCollection.find(BSONDocument()).cursor[Quotation].collect[List](100),
        Duration(2, SECONDS))
      find must haveSize(50)
      val minPopularity = find.map(_.popularity).min
      minPopularity must beEqualTo(51)
    }

  }

}
