package lectures.part3.concurrency

import java.util.Random

import scala.collection.mutable

object ThreadCommunication extends App{

  /*
  the producer-consumer problem

  producer -> [ x ] -> consumer

   */

  class SimpleContainer{
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    //producer
    def set(newValue: Int ): Unit ={
      value = newValue
    }
    //consumer
    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit ={
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] waiting ...")
      while(container.isEmpty)
        {
          println("[consumer] actively waiting ...")
        }
      println("[consumer] I have consumed "+container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced "+value)
      container.set(value)
    })

    consumer.start()
    producer.start()

  }

  //naiveProdCons()//wastage of while time
  //wait and notify
  def smartProdCons(): Unit ={
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting ...")
      container.synchronized{
        container.wait()
      }
      println("[consumer] I have consumed "+container.get)
    })
    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized{
        println("[producer] I have produced "+value)
        container.set(value)
        container.notify()
      }

    })
    consumer.start()
    producer.start()
  }

  smartProdCons()

  /*
  producer -> [ ? ? ? ] -> consumer (now we have a buffer)
   */
  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random

      while(true){
        buffer.synchronized{
          if(buffer.isEmpty)
            {
              println("[consumer] buffer empty, waiting ...")
              buffer.wait()
            }
          // there must be at least one value in the buffer!
          val x = buffer.dequeue()
          println("[consumer] I consumed "+ x)
          //hey producer there is empty space available
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }

    })


    val producer = new Thread(() => {
      val random = new Random
      var i = 0
      while(true){
      buffer.synchronized {
        if (buffer.size == capacity) {
          println("[producer] buffer is full so waiting ...")
          buffer.wait()

        }
        //there must be at least one empty space in the buffer
        println("[producer] producing "+i)
        buffer.enqueue(i)
        //hey consumer new food for you!
        buffer.notify()
        i += 1

      }
        Thread.sleep(random.nextInt(500))

      }
    })
    consumer.start()
    producer.start()
  }
  prodConsLargeBuffer()

}
