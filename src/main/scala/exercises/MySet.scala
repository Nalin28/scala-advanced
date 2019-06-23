package exercises

trait MySet[A] extends (A => Boolean){
  /*
  Exercise: Implement a functional set
   */
  def apply(elem: A): Boolean = contains(elem)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
EXERCISES
  - removing an element
  - difference with another set
  - intersection with another set
 */

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A] // intersection

  def unary_! : MySet[A]

}




class EmptySet[A] extends MySet[A]{

  def contains(elem: A): Boolean = false
  def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def map[B](f: A => B): MySet[B] = new EmptySet[B]()
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]()
  def filter(predicate: A => Boolean): MySet[A] = this
  def foreach(f: A => Unit): Unit = ()


  //part 2
  def -(elem: A): MySet[A] = this
  def --(anotherSet: MySet[A]): MySet[A] = this
  def &(anotherSet: MySet[A]): MySet[A]  = this

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => true)

}

//all elements of type A which satisfy a property
//{x in A | property(x)}
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A]{
  def contains(elem: A): Boolean = property(elem)
  //{x in A | property(x)}+element = {x in A | property(x) || x == element}
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem )
  //{x in A | property(x)} ++ anotherSet = {x in A | property(x) || anotherSet(x)}
  def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  //we need politelyFail because if we do _%3 we may get a finite set of [0 1 2] but what if the map still makes another implementation infinite
  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))
  def foreach(f: A => Unit): Unit = politelyFail
  def -(elem: A): MySet[A] = filter(x => x != elem)
  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

    def politelyFail = throw new IllegalArgumentException("Deep rabbit hole!")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A]{
  def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)
  def +(elem: A): MySet[A] =
    if(this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
  [1 2 3] ++ [4 5] =
  [2 3] ++ [4 5] + 1 =
  [3] ++ [4 5] + 1 + 2 =
  [] ++ [4 5] + 1 + 2 + 3 =
  [4 5 1 2 3]

   */
  def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  def map[B](f: A => B): MySet[B] = (tail map f) + f(head)
  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)
  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if(predicate(head))filteredTail + head
    else filteredTail
  }
  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }


  //part 2
  def -(elem: A): MySet[A] =
    if(head == elem) tail
    else  tail - elem + head

  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  def &(anotherSet: MySet[A]): MySet[A] = {
//    val intersectionTail = tail & anotherSet
//    if (anotherSet contains head) intersectionTail + head
//    else intersectionTail
    filter(anotherSet) // apply and contains are the same hence we can omit that
//actually & and filter is the same thing
  }


  //implement a unary_! for negation
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

}

object MySet{
  def apply[A](values: A*): MySet[A] = {
    def buildSet[A](valSeq: Seq[A],acc: MySet[A]): MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
    buildSet(values, new EmptySet[A])
  }
}

object MySetPlayground extends App{
  val s = MySet(1,2,3,4)
  s + 5 ++ MySet(-1,-2) + 3 flatMap(x => MySet(x, x*10)) filter(_%2 == 0) foreach println

  s & MySet(1,2,3) foreach println

  val negative = !s
  println(negative(2))
  println(negative(5))
  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))// all even > 4 + 5
}
