package org.incal.core.dataaccess

/**
  * Synchronous version of <code>AsyncCrudRepo</code>.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait SyncCrudRepo[E, ID] extends SyncRepo[E, ID] {

  def update(entity: E): ID

  def update(entities: Traversable[E]): Traversable[ID]

  def delete(id: ID)

  def delete(ids: Traversable[ID])

  def deleteAll
}