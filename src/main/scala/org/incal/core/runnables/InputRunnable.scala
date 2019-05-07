package org.incal.core.runnables

import scala.reflect.runtime.universe.Type

trait InputRunnable[I] {

  def inputType: Type

  def run(input: I): Unit
}