package lectures.part5.ts

object StructuralTypes extends App{

  // structural types

  type JavaCloseable = java.io.Closeable

  class HipsterCloseable{
    def close(): Unit = println("yeah yeah I'm closing")
    def closesSilently(): Unit = println("not making a sound")
  }

   // def closeQuietly(closeable: JavaCloseable OR HipsterCloseable) // ?!

  type UnifiedCloseable = {
  def close(): Unit
  } // Structural Type

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaCloseable{
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // TYPE REFINEMENTS



  type AdvancedCloseable = JavaCloseable{
  def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable{
    override def close(): Unit = println("Java closes")
    def closesSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable): Unit = advCloseable.closeSilently()
 //  closeShh(new HipsterCloseable) -> does not originate from JavaCloseable

  // using structural types as standalone types
  def altClose(closeable: {def close(): Unit}): Unit = closeable.close()

  // type-checking => duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }
  class Dog{
    def makeSound(): Unit = println("bark!")
  }
  class Car{
    def makeSound(): Unit = println("vroom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car
  // static duck-typing - if an instance(SoundMaker) conforms to a certain type then I can use it as the instance of that type(Dog, Car)

  // CAVEAT: based on reflection

  /*
  Exercises
   */
  trait CBL[+T]{
    def head: T
    def tail: CBL[T]
  }

  class Human{
    def head: Brain = new Brain
  }

  class Brain{
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: {def head: T}): Unit = println(somethingWithAHead.head)

  /*
  f is compatible with a CBL and with Human?
   */

  case object CBNil extends CBL[Nothing]{
    def head: Nothing = ???
    def tail: CBL[Nothing] = ???
  }
  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2,CBNil))
  f(new Human) // ?! T = Brain!!


  // 2.
  object HeadEqualizer{
    type Headable[T] = {def head: T}

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }
  /*
  is compatible with a CBL and with a Human?
   */

  val brainsList = CBCons(new Brain, CBNil)
  val stringList = CBCons("Brains", CBNil)

  HeadEqualizer.===(brainsList, new Human)
  // problem:
  HeadEqualizer.===(brainsList, stringList)

}
