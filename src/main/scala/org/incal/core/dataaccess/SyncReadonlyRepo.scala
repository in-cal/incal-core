package org.incal.core.dataaccess

/**
  * Synchronous version of <code>AsyncReadonlyRepo</code>.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait SyncReadonlyRepo[E, ID] {

  def get(id: ID): Option[E]

  def find(
    criteria: Seq[Criterion[Any]] = Nil,
    sort: Seq[Sort] = Nil,
    projection : Traversable[String] = Nil,
    limit: Option[Int] = None,
    skip: Option[Int] = None
  ): Traversable[E]

  def count(criteria: Seq[Criterion[Any]]) : Int
}