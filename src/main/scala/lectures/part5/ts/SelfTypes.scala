package lectures.part5.ts

object SelfTypes extends App{

  // requiring a type to be mixed in

  // we want to design an API for a music band in which every singer must know how to play an instrument

  trait Instrumentalist{
    def play(): Unit
  }

  trait Singer{ self: Instrumentalist => // whoever implements Singer to implement Instrumentalist as well
    // rest of the implementation or API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist{
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

//  class Vocalist extends Singer{
//    override def sing(): Unit = ???
//  }

  // cant do this because you cant extend Singer without Instrumentalist

  val jamesHetfield = new Singer with Instrumentalist{
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist{
    override def play(): Unit = println("(guitar solo)")
  }

  val ericClapton = new Guitarist with Singer{
    override def sing(): Unit = ???
  }

  // vs Inheritance
  class A
  class B extends A // B is an A

  trait T
  trait S {self: T =>} // S requires a T

  // CAKE PATTERN => "dependency injection"

  // DI
  class Component{
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component) // injection -> can receive ComponentA and ComponentB as well

  // CAKE PATTERN -> dependencies are checked in compile time not like DI where it is checked during run time
  trait ScalaComponent{
    // API
    def action(x: Int): String
  }
  trait ScalaDependentComponent {self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }
  trait ScalaApplication {self: ScalaDependentComponent =>}

  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  // cyclical dependencies

//  class X extends Y
//  class Y extends X

  trait X {self: Y => }
  trait Y {self: X => }
  // whosoever applies X must apply Y and vice-versa!
}
