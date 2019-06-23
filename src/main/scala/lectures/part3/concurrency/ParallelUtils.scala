package lectures.part3.concurrency

import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.immutable.ParVector
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.concurrent.forkjoin.ForkJoinPool

object ParallelUtils extends App{

  //1 - parallel collections

  val parList = List(1,2,3).par // now the list will be handled by multiple threads

  val aParVector = ParVector[Int](1,2,3)
  /*
  Seq
  Vectors
  Array
  Map - Hash, Trie
  Set - Hash, Trie
   */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 1000).toList
  val serialTime = measure{
    list.map(_ + 1)
  }
  println(serialTime)

  val parallelTime = measure{
    list.par.map(_ + 1)
  }
  println(parallelTime)

  /*
  map-reduce model -
  1. split the elements into chunks - splitter
  2. operation
  3. recombine - combiner
   */

  //fold, reduce are non-associative operators
  println(List(1,2,3).reduce(_-_))
  println(List(1,2,3).par.reduce(_-_))

  //synchronization
  var sum = 0
  List(1,2,3).par.foreach(sum += _)
  println(sum) // race conditions!

  //configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  aParVector.tasksupport = new TaskSupport{
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???

    override val environment: AnyRef = ???
  }

  //2 - atomic operations and references
  // atomic operations cannot be intercepted by another thread


  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe
  atomic.set(4) // thread-safe write
  atomic.getAndSet(5) // reads and writes in a thread-safe way
  atomic.compareAndSet(35,43)
  //if the value is 38, then set the value to 56
  //reference-equality
  atomic.updateAndGet(_+1) // update and the  get
  atomic.getAndUpdate(_+1)
  atomic.accumulateAndGet(12, _+_)// add value to actual value and then get
  atomic.getAndAccumulate(12,_+_)


}
