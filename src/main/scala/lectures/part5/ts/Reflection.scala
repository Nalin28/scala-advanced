package lectures.part5.ts

object Reflection extends App{

  // how to instantiate a class or invoke a method by calling just its name dynamically at runtime?
  // reflection + macros + quasi quotes => META PROGRAMMING

  case class Person(name: String){
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - import

  import scala.reflect.runtime.{universe => ru}

  // 1 - MIRROR -> can reflect things
  val m = ru.runtimeMirror(getClass.getClassLoader) // class loader for jvm => can load other classes at runtime
  // here we are getting the current class loader by doing getClass.getClassLoader

  // 2 - create a class object
  val clazz = m.staticClass("lectures.part5.ts.Reflection.Person") // creating a class object by name -> ClassSymbol = use its methods and constructor

  // 3 - create a reflected mirror
  val cm = m.reflectClass(clazz) // takes the class symbol for use

  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod // method Symbol

  // 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  // 6 - invoke the constructor
  val instance = constructorMirror.apply("John")

  println(instance)

  // I have an instance
  val p  =  Person("Mary") // from the wire as a serialized object
  // method name computed from somewhere else
  val methodName = "sayMyName"
  // 1 - mirror
  // 2 - reflect the instance
  val reflected = m.reflect(p)
  // 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method
  val method = reflected.reflectMethod(methodSymbol)
  // 5 - invoke the method

  method.apply()

  // type erasure

  // pp #1 : differentiate types at runtime -> at compile time generic types are erased
  val numbers = List(1,2,3)
  numbers match{
    case listOfStrings: List[String] => println("list of strings")
    case listOfNumbers: List[Int] => println("list of numbers")
  }
  // we get list of strings because the compiler will not differentiate between generics

  // pp #2 : limitations on overloads
  // def processList(list: List[Int]): Int = 43
  // def processList(list: List[String]): Int= 45

  // TypeTags
  import ru._

  val ttag = typeTag[Person]
  println(ttag.tpe)

  class MyMap[K, V]

  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match{
    case TypeRef(_, _, typeArguments) => TypeArgument
    case _ => List()
  }

  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArguments(myMap) // (typeTag: TypeTag[MyMap[Int, String]])
  println(typeArgs)

  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean ={
    ttagA.tpe <: ttagB.tpe
  }

  class Animal
  class Dog extends Animal
  println(isSubtype[Dog, Animal])

  // 3 - method symbol
  val anotherMethodSymbol = typeTag[Person],tpe.decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method
  val sameMethod = reflected.reflectMethod(anotherMethodSymbol)
  // 5 - invoke the method
  sameMethod.apply()
  
}
