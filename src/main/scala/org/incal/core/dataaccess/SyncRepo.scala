package org.incal.core.dataaccess

/**
  * Synchronous version of <code>AsyncRepo</code>.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait SyncRepo[E, ID] extends SyncReadonlyRepo[E, ID] {

  def save(entity: E): ID

  def save(entities: Traversable[E]): Traversable[ID] =
    entities.map(save)

  def flushOps
}