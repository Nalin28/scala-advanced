package lectures.part4.implicits

object TypeClasses extends App{
// takes a type and sees what operations can be applied to that type
trait HTMLWritable{

    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable{
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/></div>"
  }

  User("John", 22, "john.snow").toHtml

  /*
  1 - for the types WE write
  2 - ONE implementation out of quite a number
   */

  // option 2 - pattern matching
  object HTMLSerializerPM{
    def serialization(value: Any) = value match{
      case User(n, a, e) =>
      // case java.util.Date =>
      case _ =>
    }
  }
  /*
  1 - lost type safety
  2 - need to modify the code every time
  3 - still ONE implementation
   */

  trait HTMLSerializer[T]{
    def serialize(user: T): String
  }
  implicit object UserSerializer extends HTMLSerializer[User]{
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/></div>"
  }
  val john = User("John", 22, "john.snow")
  println(UserSerializer.serialize(john))

  //1 - we can define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date]{
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"

  }
  //2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User]{
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // TYPE CLASS
  trait MyTypeClassTemplate[T]{
    def action(value: T): String
  }

  // part 2
  object HTMLSerializer{
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)
  }

  implicit object IntSerializer extends HTMLSerializer[Int]{
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))


}

