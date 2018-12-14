package org.incal.core

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import org.apache.commons.io.IOUtils

import scala.collection.Iterator.empty
import scala.collection.{AbstractIterator, Iterator, Traversable}
import scala.concurrent.{ExecutionContext, Future}

package object util {

  private val nonAlphanumericUnderscorePattern = "[^A-Za-z0-9_]".r

  def seqFutures[T, U](
    items: TraversableOnce[T])(
    fun: T => Future[U]
  ): Future[Seq[U]] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    items.foldLeft(Future.successful[List[U]](Nil)) {
      (f, item) => f.flatMap {
        x => fun(item).map(_ :: x)
      }
    } map (_.reverse)
  }

  def parallelize[T, U](
    inputs: Traversable[T],
    threadsNum: Int)(
    fun: T => U
  ): Future[Traversable[U]] = {
    val threadPool = Executors.newFixedThreadPool(threadsNum)
    implicit val ec = ExecutionContext.fromExecutor(threadPool)

    val futures = inputs.map(input => Future { fun(input) })
    val resultFuture = Future.sequence(futures)

    resultFuture.map { results =>
      threadPool.shutdown()
      results
    }
  }

  def retry[T](failureMessage: String, log: String => Unit, maxAttemptNum: Int)(f: => Future[T]): Future[T] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    def retryAux(attempt: Int): Future[T] =
      f.recoverWith {
        case e: Exception =>
          if (attempt < maxAttemptNum) {
            log(s"${failureMessage}. ${e.getMessage}. Attempt ${attempt}. Retrying...")
            retryAux(attempt + 1)
          } else
            throw e
      }

    retryAux(1)
  }

  implicit class GroupMapList[A, B](list: Traversable[(A, B)]) {

    def toGroupMap: Map[A, Traversable[B]] =
      list.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  }

  implicit class GroupMapList3[A, B, C](list: Traversable[(A, B, C)]) {

    def toGroupMap: Map[A, Traversable[(B, C)]] =
      list.groupBy(_._1).map(x =>
        (x._1, x._2.map(list => (list._2, list._3)))
      )
  }

  implicit class GrouppedVariousSize[A](list: Traversable[A]) {

    def grouped(sizes: Traversable[Int]): Iterator[Seq[A]] = new AbstractIterator[Seq[A]] {
      private var hd: Seq[A] = _
      private var hdDefined: Boolean = false
      private val listIt = list.toIterator
      private val groupSizesIt = sizes.toIterator

      def hasNext = hdDefined || listIt.hasNext && groupSizesIt.hasNext && {
        val groupSize = groupSizesIt.next()
        hd = for (_ <- 1 to groupSize if listIt.hasNext) yield listIt.next()
        hdDefined = true
        true
      }

      def next() = if (hasNext) {
        hdDefined = false
        hd
      } else
        empty.next()
    }
  }

  type STuple3[T] = (T, T, T)

  def crossProduct[T](list: Traversable[Traversable[T]]): Traversable[Traversable[T]] =
    list match {
      case Nil => Nil
      case xs :: Nil => xs map (Traversable(_))
      case x :: xs => for {
        i <- x
        j <- crossProduct(xs)
      } yield Traversable(i) ++ j
    }

  def nonAlphanumericToUnderscore(string: String) =
    string.replaceAll("[^\\p{Alnum}]", "_")

  def hasNonAlphanumericUnderscore(string: String) =
    nonAlphanumericUnderscorePattern.findFirstIn(string).isDefined

  def firstCharToLowerCase(s: String): String = {
    val c = s.toCharArray()
    c.update(0, Character.toLowerCase(c(0)))
    new String(c)
  }

  // fs functions

  def listFiles(dir: String): Seq[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).toList
    else
      Nil
  }

  def writeStringAsStream(string: String, file: File) = {
    val outputStream = Stream(string.getBytes(StandardCharsets.UTF_8))
    writeByteArrayStream(outputStream, file)
  }

  def writeByteArrayStream(data: Stream[Array[Byte]], file : File) = {
    val target = new BufferedOutputStream(new FileOutputStream(file))
    try
      data.foreach(IOUtils.write(_, target))
    finally
      target.close
  }

  def writeByteStream(data: Stream[Byte], file : File) = {
    val target = new BufferedOutputStream(new FileOutputStream(file))
    try data.foreach(target.write(_)) finally target.close
  }
}