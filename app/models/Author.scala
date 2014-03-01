package models



case class Author(val name: String, val displayableName: String)
object UnknownAuthor extends Author("unknown", "unknown")

object Author {

  def apply(name: String): Author = new Author(name, name)

  def findByName(name: String): Option[Author] = defaults.find(_.name == name)

  def defaults = Seq(
    Author("Douglas Adams"),
    Author("John Adams"),
    Author("Scott Adams"),
    Author("Aeschylus"),
    Author("Aesop"),
    Author("Woody Allen"),
    Author("Maya Angelou"),
    Author("Marcus Aurelius"),
    Author("Aristotle"),
    Author("Jane Austen"),
    Author("Francis Bacon"),
    Author("Dave Barry"),
    Author("Ambrose Bierce"),
    Author("Warren Buffett"),
    Author("Lois McMaster Bujold"),
    Author("Albert Camus"),
    Author("George Carlin"),
    Author("Johnny Carson"),
    Author("G. K. Chesterton"),
    Author("Agatha Christie"),
    Author("Churchill"),
    Author("Cicero"),
    Author("Confucius"),
    Author("Calvin Coolidge"),
    Author("Ellen DeGeneres"),
    Author("Charles Dickens"),
    Author("Benjamin Disraeli"),
    Author("Thomas A. Edison"),
    Author("Albert Einstein"),
    Author("Eisenhower", "Dwight D. Eisenhower"),
    Author("Ralph Waldo Emerson"),
    Author("Euripides"),
    Author("Richard Feynman"),
    Author("Benjamin Franklin"),
    Author("Robert Frost"),
    Author("Mahatma Gandhi"),
    Author("Kahlil Gibran"),
    Author("Goethe", "Johann Wolfgang von Goethe"),
    Author("Samuel Goldwyn"),
    Author("Matt Groening"),
    Author("Mitch Hedberg"),
    Author("Oliver Wendell Holmes"),
    Author("Homer"),
    Author("Horace"),
    Author("Victor Hugo"),
    Author("Thomas Jefferson"),
    Author("Steve Jobs"),
    Author("Samuel Johnson"),
    Author("Carl Jung"),
    Author("Helen Keller"))

}