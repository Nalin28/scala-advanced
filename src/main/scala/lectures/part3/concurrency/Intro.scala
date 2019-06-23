package lectures.part3.concurrency

import java.util.concurrent.Executors

object Intro extends App{

  //JVM Threads
//  val aThread = new Thread(new Runnable {
//    override def run(): Unit = println("running in parallel!!")
//  })
//
//  //gives signal to the JVM to start a JVM thread
//  aThread.start()
////creates a JVM thread over OS thread
//
//  aThread.join()//blocks until aThread finishes running
//
//  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
//  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
//
//  threadHello.start()
//  threadGoodBye.start()
//  //different runs produce different results!
//
//  //executors
//  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("done after 1 second")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("almost done after 1 sec")
//  })
//
//  pool.shutdown() // pool will not accept any more actions
//  //pool.execute(() => println("should not appear")) // throws an exception in the calling thread
//
//  // pool.shutdownNow() // interrupts and throws exception on sleeping threads
//
//  println(pool.isShutdown)//true even before the actions are executed because shutdown means the pool does not execute any more actions
//
//  def runInParallel: Unit ={
//    var x = 0
//    val thread1 = new Thread(() => {
//      x = 1
//    })
//
//    val thread2 = new Thread(() => {
//      x = 2
//    })
//    thread1.start()
//    thread2.start()
//    println(x)
//  }
//
//  //race condition
//  for(_ <- 1 to 100) runInParallel

//  class BankAccount(@volatile var amount: Int){
//    override def toString(): String = ""+amount
//  }
//
//  def buy(account: BankAccount, things: String, price: Int): Unit ={
//    account.amount -= price
//  }
//  for(_ <- 1 to 10000)
//    {
//      val account = new BankAccount(50000)
//      val thread1 = new Thread(() => buy(account, "shoes", 3000))
//      val thread2 = new Thread(() => buy(account, "iPhone", 4000))
//
//      thread1.start()
//      thread2.start()
//      Thread.sleep(10)
//      if(account.amount != 43000)println("AHA: " +account.amount)
//      //println()
//    }
//
//  //option 1# use synchronized
//  def buySafe(account: BankAccount, thing: String, price: Int) =
//    account.synchronized{
//      //no two threads can evaluate this at the same time
//      account.amount -= price
//      println("I've bought "+thing)
//      println("my account is now "+account)
//    }

  //option 2# @volatile // all writes and reads to the particular var or val are synchronized


  /*
  Exercises

  1) Construct 50 "inception" threads
      Thread1 -> Thread2 -> Thread3
      println("hello from #3")

      in REVERSE order
   */
  def inceptionThread(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if(i < maxThreads) {
      val newThread = inceptionThread(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello from the thread $i")
  })

  inceptionThread(50).start
  //2)
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  //println(threads)
  threads.foreach(_.join)
  println(x)


  //what is the biggest possible for x? 100
  //what is the smallest value possible? 0 // because if all threads in parallel read x = 0 then the smallest value of a thread will be 1 (0+1)

  //3) sleep fallacy

  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "scala is awesome"
  })
  message = "scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join()
  println(message)

  /*
  what is the value of message? almost always "scala is awesome"
  is it guaranteed? but NOT GUARANTEED
  why? or why not?

  (main thread)
    message = "scala sucks"
    awesomeThread.start()
    sleep() - relives execution
  (awesome thread)
    sleep() - relives execution
  (OS gives the CPU to some important thread - takes CPU for more than 2 sec)
  (OS gives the CPU back to the MAIN thread)
    println("scala sucks")
  (OS gives the CPU the awesomeThread)
    message = "scala is awesome"
  TOO LATE!!!
   */

  //how do we fix this?
  //synchronizing does not work here -> only works for concurrent transactions and not for parallel
  //we use join
}
