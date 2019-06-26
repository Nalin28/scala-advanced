package lectures.part5.ts

object RockingInheritance extends App{

  // convenience
  trait Writer[T]{
    def write(value:T): Unit
  }

  trait Closeable{
    def close(status: Int): Int
  }

  trait GenericStreams[T]{
    // some methods
    def foreach(f: T => Unit): Unit
  }

  def processorStream[T](stream: GenericStreams[T] with Closeable with Writer[T]): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem

  trait Animal{def name: String}
  trait Lion extends Animal{
    override def name: String = "lion"
  }
  trait Tiger extends Animal{
    override def name: String = "tiger"
  }
  // trait Mutant extends Lion with Tiger // since its a trait we do not need to override the def name method
  class Mutant extends Lion with Tiger // still compiles

  val m = new Mutant()

  println(m.name) // tiger

  /*
 Mutant
 extends Animal with {override def name: String = "lion"}
 with {override def name: String = "tiger"}

 LAST OVERRIDE GETS PICKED
   */

  // the super problem + type linearization

  trait Cold{
    def print = println("Cold")
  }

  trait Green extends Cold{
    override def print: Unit = {
      println("Green")
      super.print
    }
  }

  trait Blue extends Cold{
    override def print: Unit = {
      println("Blue")
      super.print
    }
  }

  class Red {
    def print = println("Red")
  }

  class White extends Red with Green with Blue{ // AnyRef 
    override def print: Unit = {
      println("White")
      super.print
    }
  }

  val color = new White
  color.print


}
