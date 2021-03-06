package org.incal.core.dataaccess

/**
  * Criterion to search by, such as "equals", "in", "<", and ">".
  *
  * @param T type of the value to compare a given field with
  *
  * @since 2018
  */
sealed abstract class Criterion[+T] {                     // had to roll back to abstract class... later switch to trait
  val fieldName: String                                   // TODO: would need to be changed to fieldNames
  val value: T
  def copyWithFieldName(fieldName: String): Criterion[T]  // TODO: change name and impl to copyWithFieldPrefix(prefix: String) or remove completely and implement with casing where needed (used only by Mongo)
}

case class EqualsCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class EqualsNullCriterion(fieldName: String) extends Criterion[Unit] {
  override val value = ()
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class RegexEqualsCriterion(fieldName: String, value: String) extends Criterion[String] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class RegexNotEqualsCriterion(fieldName: String, value: String) extends Criterion[String] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class NotEqualsCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class NotEqualsNullCriterion(fieldName: String) extends Criterion[Unit] {
  override val value = ()
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class InCriterion[V](fieldName: String, value: Seq[V]) extends Criterion[Seq[V]] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class NotInCriterion[V](fieldName: String, value: Seq[V]) extends Criterion[Seq[V]] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class GreaterCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class GreaterEqualCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class LessCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}
case class LessEqualCriterion[T](fieldName: String, value: T) extends Criterion[T] {
  override def copyWithFieldName(fieldName: String) = copy(fieldName = fieldName)
}

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