package lectures.part5.ts

object TypeMembers extends App{

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection{
    type AnimalType // abstract type member ->  use it in variable or value definitions in method signatures
    type BoundedAnimal <: Animal // upper bounded with Animal
    type SuperBoundedAnimal >: Dog <: Animal // lower bounded with Dog and upper bounded with Animal
    type AnimalC = Cat // type aliases
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ???
  // val cat: ac.BoundedAnimal = new Cat -> compiler does'nt know which animal it is, may be crocodile

  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // alternative to generics
  trait MyList{
    type T
    def add(element: T): MyList
  }
  class NonEmptyList(value: Int) extends MyList{
    override type T = Int
    def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat
  //new CatsType

  /*
  Exercise - enforce a type to be applicable to SOME TYPES only
   */
  trait MList{
    type A
    def head: A
    def tail: MList
  }

  trait ApplicableToNumbers{
    type A <: Number
  }
  // not ok -> now wont work since constraints have been applied
  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
    type A = String
    def head =hd
    def tail = tl
  }
  // ok
  class IntList(hd: Int, tl: CustomList) extends MList with ApplicableToNumbers {
    type A = Int
    def head =hd
    def tail = tl
  }

  // Number
  // type member and type member constraints (bounds)



}
