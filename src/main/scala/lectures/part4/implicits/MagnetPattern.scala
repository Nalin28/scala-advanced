package lectures.part4.implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App{

  // magnet pattern is a solution to some of the problems triggered by method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor{
    def receive(statuscode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T: Serializer](message: T): Int
    def receiver[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int -> not compilable because same signature and both receive a future
    // lot of overloads
  }
  /*
  1 - type erasure
  2 - lifting does not work for all overloads
    val receiveFV = receive _ ?? -> request ? statusCode ?
  3 - code duplication
  4 - type inference and default args
    actor.receive(?!)
   */

  trait MessageMagnet[Result]{
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int]{
    def apply(): Int = {
      //logic for handling a P2PRequest
      println("Handling a P2P request")
      42
    }
  }
  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int]{
    def apply(): Int = {
      //logic for handling a P2PRequest
      println("Handling a P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // 1 - no more type erasure problems
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int]{
    override def apply(): Int = 2
  }
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int]{
    override def apply(): Int = 3
  }
  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))
  // the compiler looks for implicit types before the patterns are erased

  // 2 - lifting works

  trait MathLib{
    def add1(x: Int) = x+1
    def add1(s: String) = s.toInt + 1
    // add1 overloads
  }
  // magnetize
  trait AddMagnet{
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x+1
  }
  implicit class AddString(x: String) extends AddMagnet {
    override def apply(): Int = x.toInt + 1
  }

  val addFV = add1 _
  println(addFV(1))
  println(addFV("3"))

  /*
  Drawbacks
  1 - verbose
  2 - harder to read
  3 - you cant name or place default arguments
  4 - call be name does not work correctly
  (exercise: prove it!)(hint: side effects)

   */

  class handler{
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // other overloads

  }

  trait HandleMagnet{
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet{
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }
  def sideEffectMethod(): String ={
    println("Hello Scala")
    "hahaha" // new StringHandle("hahaha")
  }

  handle(sideEffectMethod())

  handle{
    println("Hello, Scala")
    "hahaha" // only this value is implicitly called but not the side effect
  }

}
