package org.incal.core.dataaccess

object Operator {
  sealed trait Operator
  case object AND extends Operator
  case object OR extends Operator
}

