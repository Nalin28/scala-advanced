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
  println(HTMLSerializer.serialize(john))

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
    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer //#
  }

  implicit object IntSerializer extends HTMLSerializer[Int]{
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  //access to the entire type class interface
  println(HTMLSerializer[User].serialize(john)) //#

  // part 3
  implicit class HTMLEnrichment[T](value: T){
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML) // println(new HTMLEnrichment[User](john).toHTML(UserSerialize))
/*
  - extend to any new type
  - choose implementation
  - super expressive
 */

  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer)) // flexible
  /*
  - type class itself --- HTMLSerializer[T] { ... }
  - type class instances (some of which are implicit) --- UserSerializer, IntSerializer
  - conversion with implicit classes --- HTMLEnrichment
   */

  //context bounds
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  // or
  def htmlSugar[T: HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body>${content.toHTML(serializer)}</body></html>"
    // now we can explicitly use the serializer API
  }

  // implicitly
case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0774")

  // in some other part of the code
  val standardPerms = implicitly[Permissions] // to surface the implicit being used

}

