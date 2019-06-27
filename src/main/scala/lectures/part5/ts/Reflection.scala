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
  
}
