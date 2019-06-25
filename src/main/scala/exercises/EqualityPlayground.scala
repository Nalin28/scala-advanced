package exercises

import lectures.part4.implicits.TypeClasses.User

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
}
