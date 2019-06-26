package lectures.part5.ts

object PathDependentTypes extends App{

  class Outer{
    class Inner
    object InnerObject
    type InnerType

    def print(i : Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String // alias
    2
  }

  // types can be only defined as aliases inside anything but classes and traits

  // per-instance
  val outer = new Outer
  val inner = new outer.Inner // outer.Inner is a TYPE

  val oo = new Outer
  // val otherInner: oo.Inner = new outer.Inner

  outer.print(inner)
  // oo.print(inner) -> not ok

  // path-dependent types

  // Outer#Inner
  outer.printGeneral(inner)
  oo.printGeneral(inner)

  /*
  Exercise
  DB keyed by Int or String, but maybe others
   */
  /*
  use path-dependent types
  abstract type members and/ or type aliases
   */

  trait ItemLike{
    type Key
  }
  trait Item[K] extends ItemLike{
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok
  // get[IntItem]("scala") // not ok



}
