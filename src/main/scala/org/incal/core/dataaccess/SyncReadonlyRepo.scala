package org.incal.core.dataaccess

import scala.concurrent.Future

/**
  * Synchronous version of <code>AsyncReadonlyRepo</code>.
  *
  * @tparam E type of entity
  * @tparam ID type of identity of entity (primary key)
  */
trait SyncReadonlyRepo[E, ID] {

  def get(id: ID): Option[E]

  def find(
    criteria: Option[CriteriaTree] = None,
    sort: Seq[Sort] = Nil,
    projection: Traversable[String] = Nil,
    limit: Option[Int] = None,
    skip: Option[Int] = None
  )

  def count(criteria: Option[CriteriaTree])

  def exists(id: ID): Boolean
}