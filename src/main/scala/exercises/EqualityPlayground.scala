package exercises

import lectures.part4.implicits.TypeClasses.{User, john}

object EqualityPlayground {

  /*
  Equality
   */

  trait Equal[T]{
    def equal(a: T, b: T): Boolean
  }

  implicit object NameEqual extends Equal[User]{
    override def equal(a: User, b: User): Boolean = a.name == b.name
  }

  object NameAndEmailEqual extends Equal[User]{
    override def equal(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object Equal{
    def equal[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(a, b)
  }

  val anotherJohn = User("John", 45, "yo.yo")

  println(Equal(john, anotherJohn))
}
