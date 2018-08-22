package org.incal.core.dataaccess

import akka.stream.scaladsl.Source

/**
  * Synchronous version of <code>AsyncStreamRepo</code>.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait SyncStreamRepo[E, ID] extends SyncRepo[E, ID] {
  def stream: Source[E, _]
}