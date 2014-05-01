package db

import reactivemongo.bson.{BSONDateTime, BSONString, BSONDocument}
import reactivemongo.core.commands.RawCommand
import scala.concurrent.ExecutionContext


object StatsMapReduce {
  def apply() = new StatsMapReduce {}
}

trait StatsMapReduce extends Mongo {

  val outputCollection = "stats_quotations"

  def run(lastUpdate: Long)(implicit exec: ExecutionContext) = db.command(RawCommand(mapReduceCommand(lastUpdate)))

  def mapReduceCommand(lastUpdate: Long) = BSONDocument(
    "mapreduce" -> BSONString("raw_quotations"),
    "map" -> BSONString(mapFunction),
    "reduce" -> BSONString(reduceFunction),
    "out" -> BSONDocument("reduce" -> BSONString(outputCollection)),
    "query" -> BSONDocument("timestamp" -> BSONDocument("$gte" -> BSONDateTime(lastUpdate))))

  def mapFunction =
    """function() {
      |	var key = {
      |				quote: this.quote,
      |				author: this.author,
      |       lang: this.lang
      |				};
      |	var value = 1
      |	emit(key, value);
      |};""".stripMargin


  def reduceFunction =
    """function(key, values) {
      |	reducedObject = 0
      |	values.forEach(function(value){
      |		reducedObject += value;
      |	});
      |	return reducedObject;
      |};""".stripMargin


}
