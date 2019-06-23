package lectures.part2.as

object CurriesPAF extends App{

  //curried functions
  def superAdder: Int => Int => Int =
    x => y => x+y

  val add3 = superAdder(3)// y => 3 + y
  println(add3(5))
  println(superAdder(3)(5)) //curried function

  //METHOD!
  def curriedAdder(x: Int)(y: Int): Int = x + y //curried method
  //this is an Int => Int method which returns to a val and not an unimplemented method that is a val
  val add4: Int => Int = curriedAdder(4)
  //to create functions out of methods so that we can use function values and not functions in HOF's
  //ETA-EXPANSION = lifting
  //functions != methods(JVM limitation)

  def inc(x : Int) = x + 1
  List(1,2,3).map(inc)//this is eta-expanded to x => inc(x)

  //Partial function application
  val add5 = curriedAdder(5) _ // the _ tells compiler that do an eta-expansion for me so that I get an Int => Int function value
  println(add5)

  //EXERCISES
  def simpleAddFunction = (x : Int, y : Int) => x+y
  def simpleAddMethod(x: Int, y: Int) = x+y
  def curriedAddMethod(x: Int)(y: Int) = x+y

  //add7: Int => Int = y => 7 + y
  //as many different implementations of add7 using the above
  //be creative!

  val add7 = (x:Int)  => simpleAddFunction(7,x) // simplest and can use any of the other 2 instead of it
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = curriedAddMethod(7) _ // PAF
  val add7_4 = curriedAddMethod(7)(_)// PAF
  //y => simpleAddMethod(7,y)
  val add7_5 = simpleAddMethod(7,_: Int)// alternative syntax for turning methods into function values
  val add7_6 = simpleAddFunction(7,_: Int)

  //underscores are powerful
  def concatenator(a: String, b: String, c: String) = a+b+c
  val insertName = concatenator("Hello, I am ",_,", who are you?")// x: String => concatenator("Hello, I am ",x,", who are you?")
  println(insertName("Nalin"))

  val fillInTheBlanks = concatenator("Hello, ",_,_) //  (x,y) => concatenator("Hello, ",x,y)
  println(fillInTheBlanks("Nalin", "Scala is awesome!"))

  /*
  EXERCISES
  1. Process a list of numbers and return their string representations with different formats
      Use the %4.2f, %8.6f and %14.12f with a curried formatter function

   */
  println("%4.2f".format(Math.PI))


  def format1(a: Double)(b: String): String = b.format(a)
  val form = format1(1.2343423)_
  println(form("%4.2f"))
  //List(1,2,3).format.foreach(println)

  /*
  2. Difference between
    - functions vs methods
    - parameters: by-name vs 0-lambda
   */
  def byName(n: => Int) = n+1
  def byFunction(f:() => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
  calling byName and byFunction
    -Int
    -method
    -parenMethod
    -lambda
    -PAF
   */
  byName(23)
  byName(method)
  byName(parenMethod())//USES THE FUNCTION
  byName(parenMethod) // uses the value returned by parenMethod instead of the function itself
  //byName(() => 42) --> not the same value type
  byName((()=> 42)()) // works since the anonymous function is called

  byFunction(() => 23)
  //byFunction(method)//wrong since the compiler does not do eta-expansion for methods(accessors) without parameters that boil down to a value
  byFunction(parenMethod)//compiler does eta-expansion here

}
