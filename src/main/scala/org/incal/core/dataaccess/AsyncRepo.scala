package org.incal.core.dataaccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Generic asynchronous trait for a repo allowing read/find as well save operations (but without update or delete).
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait AsyncRepo[E, ID] extends AsyncReadonlyRepo[E, ID] {

  def save(entity: E): Future[ID]

  def save(entities: Traversable[E]): Future[Traversable[ID]] =
    Future.sequence(entities.map(save))

  def flushOps: Future[Unit]
}