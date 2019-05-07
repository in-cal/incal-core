package org.incal.core.runnables

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


trait InputFutureRunnable[I] extends InputRunnable[I] {

  protected val timeout = 100 hours

  def runAsFuture(input: I): Future[Unit]

  override def run(input: I) = Await.result(runAsFuture(input), timeout)
}