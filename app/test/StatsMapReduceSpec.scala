import db.StatsMapReduce
import org.joda.time.format.DateTimeFormat
import reactivemongo.api.MongoDriver
import org.specs2.mutable.Specification
import reactivemongo.bson.BSONDocument
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import scala.util.Success


class StatsMapReduceSpec extends Specification {

  val statsMapReduce = new StatsMapReduce {
    def driver = new MongoDriver
    def connection = driver.connection(List("localhost"))
    override def db = connection("mydb")
  }

  val formatter = DateTimeFormat.forPattern("YYYY-MM-dd")

  "A StatsMapReduceJob" should {

    "run a mapreduce job" in {
      val BSONDocument(ret) = Await.result(statsMapReduce.run(0L), Duration(10, SECONDS))
      val job = ret.toList.collect {
        case Success(vt) => vt
      }
      job must haveSize(4)
    }

  }

}
