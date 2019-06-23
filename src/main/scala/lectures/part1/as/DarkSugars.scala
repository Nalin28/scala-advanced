package lectures.part1.as

import scala.util.Try

object DarkSugars extends App {

  //syntax sugar #1: methods with single params
  def singleArgMethod(arg: Int) : String = s"$arg little ducks ..."

  val description = singleArgMethod {
    //write some complex code
    42
  }

  val aTryInstance = Try{ // java's try {...}
    throw new RuntimeException
  }

  //similarly

  List(1,2,3).map {x =>
  x + 1
  }

  //syntax sugar #2: single abstract method pattern (instances of traits with a single method can be changed to lambdas)
  trait Action{
    def act(x: Int): Int
  }

  val anInstance: Action = new Action{
    override def act(x: Int): Int = x+1
  }

  //or

  val aFunkyInstance: Action = (x: Int) => x+1

      //example: instantiating traits with Runnables

  val aThread = new Thread(new Runnable {//how java does it
    override def run(): Unit = println("hello, scala")
  })

  //or

  val aSweeterThread = new Thread(() => println("sweet scala"))

  abstract class AnAbstractType{
    def implemented: Int = 23
    def f(a: Int): Unit

  }

  val anAbstractInstance: AnAbstractType = (x: Int) => println(s"sweet $x")

  //syntax sugar #3: the :: and the #:: methods are special

  val prependedList = 2 :: List(3,4)//means List(3,4).::(2)
  //because having a : means the method is right associative else left associative
  //last character decides the associativity of the method
  1 :: 2 :: 3 :: List(4,5) // first 3 nd list and so on

  //example

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: new MyStream[Int]

  //syntax sugar #4: multi-word method naming

  class TeenGirl(name: String){
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"

  //syntax sugar #5: infix types
  class Composite[A,B]

  val composite: Composite[Int, String] = ???
  //or
  val funComposite: Int Composite String = ???

  class -->[A,B]

  val towards: Int --> String = ???

  //syntax sugar #6: update() is very special,much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 //rewritten to anArray.update(2,7)
  //used in mutable collections
  //remember apply() AND update()!

  //syntax sugar #7: setters for mutable containers

  class Mutable{
    private var internalMember: Int = 0
    def member = internalMember
    def member_=(value: Int): Unit =
    internalMember = value

  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42//rewritten as aMutableContainer.member_ = 42






}
