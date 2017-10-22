package db

import reactivemongo.bson.{BSONDateTime, BSONString, BSONDocument}
import reactivemongo.core.commands.RawCommand
import scala.concurrent.ExecutionContext


object StatsMapReduce {
  def apply() = new StatsMapReduce {}
}

trait StatsMapReduce extends Mongo {

  def run(lastUpdate: Long)(implicit exec: ExecutionContext) = db.command(RawCommand(mapReduceCommand(lastUpdate)))

  private def mapReduceCommand(lastUpdate: Long) = BSONDocument(
    "mapreduce" -> BSONString(rawQuotationsColl.name),
    "map" -> BSONString(mapFunction),
    "reduce" -> BSONString(reduceFunction),
    "out" -> BSONDocument("reduce" -> BSONString(statsQuotationsColl.name)),
    "query" -> BSONDocument("timestamp" -> BSONDocument("$gte" -> BSONDateTime(lastUpdate))))

  private def mapFunction =
    """function() {
      |	var key = {
      |				quote: this.quote,
      |				author: this.author,
      |       lang: this.lang
      |				};
      |	var value = 1
      |	emit(key, value);
      |};""".stripMargin


  private def reduceFunction =
    """function(key, values) {
      |	reducedObject = 0
      |	values.forEach(function(value){
      |		reducedObject += value;
      |	});
      |	return reducedObject;
      |};""".stripMargin

}
