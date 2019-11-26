package org.incal.core.dataaccess

import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

import scala.concurrent.{ExecutionContext, Future}

object CrudRepoExtra {

  implicit class CrudInfixOps[E](val dataSetRepo: AsyncCrudRepo[E, _]) extends AnyVal {

    def saveAsStream(
      source: Source[E, _],
      spec: StreamSpec = StreamSpec())(
      implicit materializer: Materializer, executionContext: ExecutionContext
    ): Future[Unit] = {

      val finalStream = asyncStream(
        source,
        dataSetRepo.save(_: E),
        Some(dataSetRepo.save(_ : Traversable[E])),
        spec
      )

      finalStream.runWith(Sink.ignore).map(_ => ())
    }

    def updateAsStream(
      source: Source[E, _],
      spec: StreamSpec = StreamSpec())(
      implicit materializer: Materializer, executionContext: ExecutionContext
    ): Future[Unit] = {

      val finalStream = asyncStream(
        source,
        dataSetRepo.update(_: E),
        Some(dataSetRepo.update(_ : Traversable[E])),
        spec
      )

      finalStream.runWith(Sink.ignore).map(_ => ())
    }
  }

  private def asyncStream[T, U](
    source: Source[T, _],
    process: T => Future[U],
    batchProcess: Option[Traversable[T] => Future[Traversable[U]]] = None,
    spec: StreamSpec = StreamSpec())(
    implicit materializer: Materializer, executionContext: ExecutionContext
  ): Source[U, _] = {
    val parallelismInit = spec.parallelism.getOrElse(1)

    def buffer[T](stream: Source[T, _]): Source[T, _] =
      spec.backpressureBufferSize.map(stream.buffer(_, OverflowStrategy.backpressure)).getOrElse(stream)

    val batchProcessInit = batchProcess.getOrElse((values: Traversable[T]) => Future.sequence(values.map(process)))

    spec.batchSize match {

      // batch size is defined
      case Some(batchSize) =>
        buffer(source.grouped(batchSize))
          .mapAsync(parallelismInit)(batchProcessInit).mapConcat(_.toList)

      case None =>
        buffer(source)
          .mapAsync(parallelismInit)(process)
    }
  }
}

case class StreamSpec(
  batchSize: Option[Int] = None,
  backpressureBufferSize: Option[Int]  = None,
  parallelism: Option[Int] = None
)