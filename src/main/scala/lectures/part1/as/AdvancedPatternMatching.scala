package lectures.part1.as

object AdvancedPatternMatching extends App{

  val numbers = List(1,2)

  val description = numbers match{
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }
/*
what is available for pattern matching:
  -constants
  -wildcards
  -tuples
  -case classes
  -magic like above
 */
  //what if we can make only a class and not a case class? how do we match?
  class Person(val name: String,val  age: Int)

  object Person{
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] = Some(if(age < 23) "underage" else "fine")
  }

  val bob = new Person("Bob",22)

  val greeting = bob match{
    case Person(n,a) => println(s"hey $n of age $a yo!")
  }
  val legalStatus = bob.age match{
    case Person(status) => s"my legal status in $status"
  }

  println(greeting)
  println(legalStatus)
  
  
  object even{
    def unapply(arg: Int): Boolean = (arg%2 == 0)
  }

  object singleDigit{
    def unapply(arg: Int): Boolean = (arg > -10 && arg < 10)
  }

val number = 8
  val matchedNumber = number match{
    case even() => "even number"
    case singleDigit() => "single digit"
    case _ => "no match found"
  }

  println(matchedNumber)




  //infix patterns

  case class Or[A,B](a: A, b:B) //Either
  val either = Or(2,"two")
  val humanDescription = either match{
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)

  //decomposing sequences
  val vararg = numbers match{
    case List(1,_*) => "starting with 1"//here max 2 params can be given
    case _ =>
  }

  abstract class MyList[+A]{
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList{
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1,Cons(2,Cons(3,Empty)))

  val decomposed = myList match{
    case MyList(1,2,_*) => "STARTING WITH 1,2"//any number of parameters can be matched
    case _ =>
  }
  println(decomposed)

  //custom return types for unapply
  //isEmpty: Boolean, get: something.

  abstract class Wrapper[T]{
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper{
    def unapply(person: Person): Wrapper[String] = new Wrapper[String]{
      override def isEmpty: Boolean = false

      override def get: String = person.name
    }
  }

  println(bob match{
    case PersonWrapper(n) => s"this persons name is $n"
    case _ =>
  })



}
