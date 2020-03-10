package org.incal.core.dataaccess

sealed trait Operator
case object AND extends Operator
case object OR extends Operator
