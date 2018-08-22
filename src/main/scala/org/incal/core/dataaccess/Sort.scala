package org.incal.core.dataaccess

/**
  * Definition of asc/desc sort by a given field name (column).
  *
  * @author Peter Banda
  * @since 2018
  */
trait Sort {
  val fieldName : String
}

case class AscSort(fieldName : String) extends Sort
case class DescSort(fieldName : String) extends Sort