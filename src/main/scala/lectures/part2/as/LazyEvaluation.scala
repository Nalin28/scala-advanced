package lectures.part2.as

import com.sun.tools.javadoc.Start

object LazyEvaluation extends App{

  //lazy delays the evaluation of values - but only for the first time
  lazy val x : Int = throw new  RuntimeException()

  lazy val y = {
    println("hello")
    42
  }

  println(y)
  println(y)

  //examples of implications:
  //side effects
  def sideEffectCondition: Boolean = {
    println("hello")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition)"yes" else "no")
  //hello will not be printed since it is lazy and the compiler knows that simpleCondition which is false is clumped with lazyCondition in && so the lazy evaluation is skipped

  //in conjunction with call by name
  def byNameMethod(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1
  }
  def retrieveMagicValue: Int ={
    //side effect or a long computation
    println("waiting")
    Thread.sleep(1000);
    42
  }

  println(byNameMethod(retrieveMagicValue))// waits more than 3 sec because the value n is evaluated 3 times
  // so we use lazy declaration so that it is evaluated once
  // this is CALL BY NEED

  //filtering with lazy vals
  def lessThan30(i: Int): Boolean ={
  println(s"$i is less than 30")
  i < 30
  }
  def greaterThan20(i: Int): Boolean ={
    println(s"$i is greater than 20")
    i > 20
  }

  val numbers = List(1,25,40,5,23)
  val lt30 = numbers.filter(lessThan30)//List(1,25,5,23)
  val gt20 = lt30.filter(greaterThan20)//List(25,23)
  println(gt20)

  val lt30lazy = numbers.withFilter(lessThan30)
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println
  gt20lazy foreach println


  //for-comprehensions use withFilter with guards
  for{
    a <- List(1,2,3) if(a%2 == 0)
  }yield a + 1
  //or
  List(1,2,3).withFilter(_%2 == 0).map(_ + 1) // List[Int]

  /*
  EXERCISE:
  Implement a lazily evaluated, singly linked STREAM of elements.
  naturals = MyStream(1)(x => x+1) == stream of natural numbers (potentially infinite!) => println will crash
  naturals.take(100)//lazily evaluated stream of the first 100 naturals(finite stream) => will println
  naturals.map(_*2) == stream of all even numbers (potentially infinite!)
   */

  abstract class MyStream[+A]{
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B>:A](element: B): MyStream[B] // prepend operator
    def ++[B>:A](anotherStream: MyStream[B]): MyStream[B]//concatenate 2 streams

    def foreach(f:A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter[B](predicate: A => Boolean): MyStream[B]

    def take(n: Int): MyStream[A]//takes first n elements out of this stream
    def takeAsList(n: Int): List[A]

  }

  object MyStream{
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }

}
