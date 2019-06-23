package lectures.part2.as

object PartialFunctions extends App{

  val aFunction = (x: Int) => x+1//Function[Int,Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if(x == 1)42
    else if (x == 2) 56
    else if(x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match{
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  //{1,2,5} => Int
  //since {1,2,5} is a subset of Int and the function is partially from Int => Int hence it is called partial function
  //we can write it as

  val aPartialFunction: PartialFunction[Int,Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }//partial function value

  println(aPartialFunction(2))
  //println(aPartialFunction(2345))  --> match error

  //PF Utilities
  println(aPartialFunction.isDefinedAt(67))

  //lift
  val lifted = aPartialFunction.lift//Int => Option[Int]
  println(lifted(67))

  val pfChain = aPartialFunction.orElse[Int, Int]{
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  //pf extend normal functions

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 99
  }

  println(aMappedList)
  /*
  Note: PF can have only ONE parameter type
   */

  /*
  Exercises
  1. construct a PF instance yourself
  2. dumb chatbot as an instance
   */

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 78
      case 5 => 99
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x ==2 || x == 5
  }

  val chatBot: PartialFunction[String,String] = {
    case "hey" => "hey pal"
    case "hi" => "hi bro"
  }

//scala.io.Source.stdin.getLines().foreach{
//  (line => println("chat bot says: " +chatBot(line)))
//}

  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)

}


