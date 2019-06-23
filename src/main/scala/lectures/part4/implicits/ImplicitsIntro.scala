package lectures.part4.implicits

object ImplicitsIntro extends App{

  val pair = "Daniel" -> "555"
  val intPair = 1 -> 2 // implicit ArrowAssoc method that converts it to tuple

  case class Person(name: String){
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet)// the compiler looks for anything that can complete the greet method on the string
  //println(fromStringToPerson("Peter").greet)

  // If uncommented then the compiler will get confused about which implicit to get
//  class A{
//    def greet: Int = 2
//  }
//  implicit def fromStringToA(str: String): A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

  increment(2)
  // NOT default args

}
