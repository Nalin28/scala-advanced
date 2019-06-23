package lectures.part3.concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
//important for futures
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FuturesAndPromises extends App{

  //futures are a functional way of computing on another thread or in parallel

  def calculateTheMeaningOfLife: Int ={
    Thread.sleep(2000)
    42
  }
  val aFuture = Future{
    calculateTheMeaningOfLife // calculateTheMeaningOfLife on another thread
  }// (global) which is injected by the compiler

  println(aFuture.value)// returns Option[Try[Int]] => because the future might throw and exception also it might not have finished so an Option

  println("waiting for the future")

  aFuture.onComplete(t => t match {
    case Success(meaningOfLife) => println(s"The meaning of Life is $meaningOfLife")
    case Failure(e) => println(s"The error is $e")
  }) // waits for the future to complete hence is of type Try[Int]
     //onComplete is called by SOME thread may be this thread may be some other thread

     //if we do not give Thread.sleep then the main thread completes before the future thread and has a chance to run this call back
  Thread.sleep(2000)

  //mini social network
  case class Profile(id: String, name: String){
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork{
    //"database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-gate" -> "Bill",
      "fb.id.3-dumm" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-gate"
    )

    val random = new Random()

    //API
    def fetchProfile(id: String): Future[Profile] = Future{
      //fetching from the DB
      Thread.sleep(random.nextInt(300))
      Profile(id,names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future{
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId,names(bfId))
    }


  }
  // client: Mark poke Bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")

  mark.onComplete({
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete({
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      })
    }
    case Failure(e) => e.printStackTrace()
  })
  Thread.sleep(2000)
  //ugly code and nested
  //functional composition of futures
  // map, flatMap and filter

  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriend = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  //for-comprehensions (beautiful)

  for{
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  }mark.poke(bill)
  Thread.sleep(2000)

  //fallbacks - if the id is not known then the method will throw an exception so we use recover to handle it

  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("fb.id.1-zuck").recover{
    case e: Throwable => Profile("fb.id.0-dumm", "Forever Alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("fb.id.1-zuck").recoverWith{
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dumm")
  }
  //it will put the value in the val and if the second statement also throws exception it will consider the first expression's exception
  val fallBackResult = SocialNetwork.fetchProfile("fb.id.1-zuck").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dumm"))

  // online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp{
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future{
      //simulating a database
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future{
      //simulate some processes
      Thread.sleep(500)
      Transaction(user.name, merchantName, amount,"SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String ={
      //fetch the user from the DB
      //create a transaction
      //WAIT for the transaction to finish
      val transactionStatusFuture = for{
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      }yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversion -> pimp my library
      //will wait till all futures are complete
    }
  }
  println(BankingApp.purchase("Daniel","i Phone 12", "rock the jvm",3000))

  //promises
  val promise = Promise[Int]()//controller over a future
  val future = promise.future

  //thread-1 : consumer
  future.onComplete({
    case Success(r) => println(s"[consumer] I have received $r")
  })

  val producer = new Thread(() =>{
    println("[producer] crunching numbers ...")

    Thread.sleep(500)
    //fulfilling the promise
    promise.success(42)
    println("[producer] done!")
  })

  producer.start()
  Thread.sleep(2000)


  /*
  1) fulfill a future IMMEDIATELY with a value
  2) inSequence(fa, fb) -> executes fb only after fa has finished
  3) first(fa, fb) -> either of which finishes first
  4) last(fa, fb) -> either of which finishes last
  5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  //1
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  //2
  def inSequence[A,B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)// until the first operation is complete do not get second

  //3
  def first[A](fa: Future[A], fb: Future[A]): Future[A] ={
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  //4
  def last[A](fa: Future[A], fb: Future[A]): Future[A] ={
    //1 - promise that both futures will try to complete(and fail)
    //2 - promise which the last future will complete

    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    val checkAndComplete = (result: Try[A]) => if(!bothPromise.tryComplete(result))
      lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future{
    Thread.sleep(100)
    42
  }
  val slow = Future{
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)

  Thread.sleep(2000)

  //5
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
    action()
    .filter(condition)
    .recoverWith({
      case _ => retryUntil(action, condition)
    })

  val random = new Random()

  val action = () => Future{
    Thread.sleep(100)
    val nextVal = random.nextInt(100)
    println("generated "+nextVal)
    nextVal
  }

  retryUntil(action, (x: Int) =>  x < 50).foreach(result => println("settled at " + result))

  Thread.sleep(2000)
}
