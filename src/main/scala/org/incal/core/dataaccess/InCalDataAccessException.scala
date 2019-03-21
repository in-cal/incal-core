package org.incal.core.dataaccess

class InCalDataAccessException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) = this(message, null)
}

