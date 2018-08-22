package org.incal.core.dataaccess

import akka.stream.scaladsl.Source

/**
  * Generic asynchronous trait for a repo allowing live streaming of all the saved items... works essentially as a queue.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait AsyncStreamRepo[E, ID] extends AsyncRepo[E, ID] {
  def stream: Source[E, _]
}
