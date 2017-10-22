package models

import scala.io.Source


case class Author(val name: String, val displayableName: String)
object UnknownAuthor extends Author("unknown", "unknown")

object Author {

  def apply(name: String): Author = new Author(name, name)

  def findByName(name: String): Option[Author] = defaults.find(_.name == name)

  def findInStatus(status: String) =
    defaults.find(a => status.containsCaseInsensitive(a.name))

  lazy val defaults = Source.fromFile("conf/watchlist.txt").getLines().toSeq map { l =>
    val fields = l.split(";")
    val name = fields(0)
    val displayName = fields(1)
    Author(name, displayName)
  }

}