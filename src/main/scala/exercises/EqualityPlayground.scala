package exercises

import lectures.part4.implicits.TypeClasses.{User, john}

object EqualityPlayground extends App{

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
  /*
    Exercise: Implement the TC pattern for the Equality tc.
     */


  // AD-HOC polymorphism -
  object Equal{
    def equal[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(a, b)
  }

  val anotherJohn = User("John", 45, "yo.yo")

  println(Equal(john, anotherJohn))

  /*
  Exercise - improve the Equal TC with an implicit conversion class
  ===(anotherValue: T)
  !==(anotherValue: T)
   */
  implicit class TypeSafeEqual[T](value: T){
   def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.equal(value, anotherValue)
  }

  println(john === anotherJohn)
  println(john !== anotherJohn)
/*
  john.===(anotherJohn)
  Is there a method === that takes  User type value (john)? => yes
    new TypeSafeEqual[User](john).===(anotherJohn)
  Is there an equalizer that implicitly is of type Equal[T]? yes
    new TypeSafeEqual[User](john).===(anotherJohn)(NameEqual)
 */
  /*
  TYPE SAFE
   */
  println(john == 43) // correct
  //println(john === 43) // type safe since I cant even compile this


}
