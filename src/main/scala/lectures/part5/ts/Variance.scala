package lectures.part5.ts

object Variance extends App{

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat] // if a cage contains a cat it can contain any animal

  // no - invariance
  //  class ICage[T]
  //  val icage: ICage[Animal] = new ICage[Cat]
  // val x: Int = "hello" --> the above statement is this bad

  // hell no - contra variance
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal] // if a cage can contain any animal it can also contain a cat

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // in the parameter field the compiler accepts a covariant type

  // class ContravariantCage[-T](val animal: T) // you are filling the wrong animal in the cat cage
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  // class CovariantVariableCage[+T](var animal: T) // appears in contravariant position
    /*
    val catCage: CCage[Animal] = new CCage[Cat](new Cat)
    CCage.animal = new Crocodile // cant pass another animal
     */

  // class ContravariantVariableCage[+T](var animal: T) // also in covariant position
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  class InvariantVariableCage[T](var animal: T) // ok

//  trait AnotherCovariantCage[+T]{
//    def addAnimal(animal: T) // method args are in CONTRAVARIANT POSITION
//  }

  /*
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.add(new Cat)
   */

  class AnotherContravariantCage[-T]{
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A]{
    def add[B>:A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)

  val moreAnimals = animals.add(new Cat) // ok -> MyList[Variance.Cat]
  val evanMoreAnimals = animals.add(new Dog) // ok -> MyList[Variance.Animals]

  // method args are in CONTRAVARIANT POSITIONS

  // return types
  class PetShop[-T]{
    // def get(isItaPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
    val catShop = new PetShop[Animal]{
    def get(isItaPuppy: Boolean): Animal = new Cat
    }

    val dogShop: PetShop[Dog] = catShop
    dogShop.get(true) // EVIL CAT
     */
    def get[S<:T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  // return new type that is the subtype of the actual type

  val shop: PetShop[Dog] = new PetShop[Animal]
  // val evilCat = shop.get(true, new Cat)
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

/*
Exercise
1 - Invariant, covariant, contravariant
    Parking[T](things: List[T]){
    park(vehicle: T)
    impound(vehicles: List[T])
    checkVehicles(conditions: String): List[T]
  }
2 - used someone else's API: IList[T]
3 - Parking = monad!
    - flatMap
 */
  // 1
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  class IParking[T](vehicles: List[T]){
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMaP[S](f: T => CParking[S]): CParking[S] = ???
  }

  class CParking[+T](vehicles: List[T]){
    def park[S>:T](vehicle: S): IParking[S] = ???
    def impound[S>:T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ??? // list is covariant and appears in a covariant position
    def flatMaP[S](f: T => CParking[S]): CParking[S] = ???
  }
  // actions so use this
  class XParking[-T](vehicles: List[T]){
    def park[S<:T](vehicle: S): IParking[S] = ???
    def impound[S<:T](vehicles: List[S]): XParking[S] = ???
    def checkVehicles[S<:T](conditions: String): List[S] = ???
    def flatMaP[R <: T,S](f: R => CParking[S]): CParking[S] = ??? // # (inv * inv = cov)
  }

  /*
  Rule of Thumb
    - use covariance = COLLECTION OF THINGS
    - use contra variance = GROUP OF ACTIONS
   */

    // 2
    class CParking2[+T](vehicles: IList[T]){
      def park[S>:T](vehicle: S): IParking[S] = ???
      def impound[S>:T](vehicles: IList[S]): CParking[S] = ???
      def checkVehicles[S>:T](conditions: String): IList[S] = ??? // list is covariant and appears in a covariant position
    }
  // actions so use this
  class XParking2[-T](vehicles: IList[T]){
    def park[S<:T](vehicle: S): IParking[S] = ???
    def impound[S<:T](vehicles: IList[S]): XParking[S] = ???
    def checkVehicles[S<:T](conditions: String): IList[S] = ???
  }

  // 3

}
