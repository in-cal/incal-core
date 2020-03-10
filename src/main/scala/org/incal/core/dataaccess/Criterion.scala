package org.incal.core.dataaccess


sealed trait CriteriaTree
case class Node(operator: Operator, left: CriteriaTree, right: CriteriaTree) extends CriteriaTree
sealed trait Criterion[+T] extends CriteriaTree {
  val fieldName: String
  val value: T
}

case class EqualsCriterion[T](fieldName: String, value: T) extends Criterion[T]

case class EqualsNullCriterion(fieldName: String) extends Criterion[Unit] {
  override val value: Unit = ()
}

case class RegexEqualsCriterion(fieldName: String, value: String) extends Criterion[String]

case class RegexNotEqualsCriterion(fieldName: String, value: String) extends Criterion[String]

case class NotEqualsCriterion[T](fieldName: String, value: T) extends Criterion[T]

case class NotEqualsNullCriterion(fieldName: String) extends Criterion[Unit] {
  override val value: Unit = ()
}

case class InCriterion[V](fieldName: String, value: Seq[V]) extends Criterion[Seq[V]]

case class NotInCriterion[V](fieldName: String, value: Seq[V]) extends Criterion[Seq[V]]

case class GreaterCriterion[T](fieldName: String, value: T) extends Criterion[T]

case class GreaterEqualCriterion[T](fieldName: String, value: T) extends Criterion[T]

case class LessCriterion[T](fieldName: String, value: T) extends Criterion[T]

case class LessEqualCriterion[T](fieldName: String, value: T) extends Criterion[T]

object Criterion {
  implicit class Infix(val fieldName: String) extends AnyVal {

    def #==[T](value: T) = EqualsCriterion(fieldName, value)
    def #=@[T] = EqualsNullCriterion(fieldName)

    def #!=[T](value: T) = NotEqualsCriterion(fieldName, value)
    def #!@[T] = NotEqualsNullCriterion(fieldName)

    def #~(value: String) = RegexEqualsCriterion(fieldName, value)
    def #!~(value: String) = RegexNotEqualsCriterion(fieldName, value)

    def #->[V](value: Seq[V]) = InCriterion(fieldName, value)
    def #!->[V](value: Seq[V]) = NotInCriterion(fieldName, value)

    def #>[T](value: T) = GreaterCriterion(fieldName, value)
    def #>=[T](value: T) = GreaterEqualCriterion(fieldName, value)

    def #<[T](value: T) = LessCriterion(fieldName, value)
    def #<=[T](value: T) = LessEqualCriterion(fieldName, value)
  }
}