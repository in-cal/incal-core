package org.incal.core

import scala.reflect.runtime.universe.Type
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Run as future.
  *
  * @author Peter Banda
  * @since 2018
  */
trait FutureRunnable extends Runnable {
  protected val timeout = 100 hours

  def runAsFuture: Future[Unit]

  override def run = Await.result(runAsFuture, timeout)
}

trait InputRunnable[I] {

  def inputType: Type

  def run(input: I): Unit
}

trait InputFutureRunnable[I] extends InputRunnable[I] {
  protected val timeout = 100 hours

  def runAsFuture(input: I): Future[Unit]

  override def run(input: I) = Await.result(runAsFuture(input), timeout)
}