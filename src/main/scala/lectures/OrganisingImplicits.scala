package lectures

object OrganisingImplicits extends App{

  // implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_>_)
  // implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_<_)

  println(List(1,4,5,3,2).sorted)// takes ordering as an implicit parameter
  //scala.Predef


  /*
  Implicits (used as implicit parameters):
  - val/var
  - object
  - accessor methods = def's with no parentheses
   */

  //Exercise
  case class Person(name: String, age: Int)

  implicit val alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  println(persons.sorted)

  /*
  Implicit scope
  - normal scope = LOCAL SCOPE
  - imported scope
  - companions of all types involved int the method signature
    - List
    - Ordering
    - all the types involved = A or any supertype
   */
  // def sorted[B >: A](implicit ord: Ordering[B]): List[B]

  /*
  when defining an implicit val:
    #1
    . if there is a single possible value for it
    . and you can edit the code for the type

    then define the implicit in the companion object

    #2
    . if there are many possible values for it
    . but a single good one
    . and you can edit the code for the type

    then define the good implicit in the companion
   */

  object AlphabeticNameOrdering{ // you want this
    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering{ // or this
    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.age < b.age)
  }

  //just import whichever you want
  // import AlphabeticNameOrdering._
  println(persons.sorted)

  /*
  Exercise.

  - totalPrice = most used (50%)
  - by unit count = 25%
  - by unit price = 25%

   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase{
    implicit def totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice )
  }
  object UnitCountOrdering{
    implicit def unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.unitPrice < b.unitPrice )
  }
  object UnitPriceOrdering{
    implicit def unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits < b.nUnits )
  }

  val list = List(
    Purchase(5,2.5),
    Purchase(3, 71),
    Purchase(7, 24.5),
    Purchase(10, 20)
  )


  println(list.sorted)

}
