package org.incal.core.dataaccess

import akka.stream.Materializer
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Generic asynchronous trait for a readonly repo.
  *
  * @param E type of entity
  * @param ID type of identity of entity (primary key)
  *
  * @author Peter Banda
  * @since 2018
  */
trait AsyncReadonlyRepo[+E, ID] {

  def get(id: ID): Future[Option[E]]

  /**
    * Finds all the elements matching criteria object.
    *
    * @param criteria Filtering criteria. Use Nil for no filtering / return all.
    * @param sort Sequence of asc/desc columns used for sorting. Leave at Nil for no sorting.
    * @param projection Defines which columns are supposed to be returned. Leave at Nil to return all.
    * @param limit Page limit. Use to define chunk sizes. Leave at None to use default.
    * @param skip The number of items to skip.
    * @return Future of the found items.
    */
  def find(
    criteria: Seq[Criterion[Any]] = Nil,
    sort: Seq[Sort] = Nil,
    projection: Traversable[String] = Nil,
    limit: Option[Int] = None,
    skip: Option[Int] = None
  ): Future[Traversable[E]]

  // default/dummy implementation of streaming... if supported should be overridden
  def findAsStream(
    criteria: Seq[Criterion[Any]] = Nil,
    sort: Seq[Sort] = Nil,
    projection: Traversable[String] = Nil,
    limit: Option[Int] = None,
    skip: Option[Int] = None)(
    implicit materializer: Materializer
  ): Future[Source[E, _]] = for {
    items <- find(criteria, sort, projection, limit, skip)
  } yield {
    Source.fromIterator(() => items.toIterator)
  }

  /**
    * Return the number of elements matching criteria.
    *
    * @param criteria Filtering criteria (same as <code>find</code>). Use Nil for no filtering / return all.
    * @return Number of matching elements.
    */
  def count(criteria: Seq[Criterion[Any]] = Nil): Future[Int]

  // default/dummy implementation of exists (get and check)... should be overridden if more intelligent check is available
  def exists(id: ID): Future[Boolean] =
    get(id).map(_.isDefined)
}