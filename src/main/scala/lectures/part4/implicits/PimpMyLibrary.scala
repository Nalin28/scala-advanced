package lectures.part4.implicits

object PimpMyLibrary extends App{

  // 2.isPrime

  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value%2 == 0
    def sqrt: Double = Math.sqrt(value)
    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit =
        if(n <= 0) ()
      else
          {
            function()
            timesAux(n-1)
          }
      timesAux(value)
    }
    def *[B](a: List[B]): List[B] = {
      def helper(n: Int, acc: List[B] = Nil): List[B] ={
        if(n <= 0) acc
        else
          helper(n-1, acc ::: a)
      }
      helper(value)
    }
  }
  implicit class RicherInt(richInt: RichInt){
    def isOdd: Boolean = richInt.value%2 != 0
  }

  new RichInt(42).sqrt

  42.isEven // type enrichment = pimping
  // similarly
  1 to 10
  import scala.concurrent.duration._
  3.seconds

  // 42.isOdd
  // wont work since compiler does'nt do multiple implicit searches it can wrap in Rich but not Richer

  /*
  Enrich String class
  - asInt
  - encrypt
    - "John" -> "Lqjp"

  Keep enriching the Int class
  - times(function)
  3.times(() => ...)
  - *
  3*List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class RichString(value: String){
    def asInt: Int = Integer.valueOf(value)
    def encrypt(cypherDistance : Int): String = value.map(c => (c+cypherDistance).asInstanceOf[Char])
  }

  println("3".asInt + 4)
  println("nalin".encrypt(3))
  3.times(() => println("Scala Rocks!"))
  println(2 * List("nalin","munshi"))

  // "3" / 4

  implicit def stringToInt(string: String): Int = Integer.valueOf(string)

  println("6" / 2) // stringToInt("6") / 2
  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  implicit def intToBoolean(i: Int): Boolean = i == 1
  /*
  if(n) do something
  else do something else
   */

  val aConditionedValue = if (3) "OK" else "Something Wrong"
  println(aConditionedValue)

  /*
  - keep type enrichment to implicit classes and type classes
  - avoid implicit def's as much as possible
  - package implicits clearly, bring into scope only what you need
  - IF you need conversions, make them specific
   */
}
