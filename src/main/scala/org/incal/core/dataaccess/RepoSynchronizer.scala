package org.incal.core.dataaccess

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.Duration

object RepoSynchronizer {

  def apply[E, ID](asyncRepo : AsyncReadonlyRepo[E, ID], timeout: Duration) : SyncReadonlyRepo[E, ID] =
    new SyncReadonlyRepoAdapter(asyncRepo, timeout)

  def apply[E, ID](asyncRepo : AsyncRepo[E, ID], timeout: Duration) : SyncRepo[E, ID] =
    new SyncRepoAdapter(asyncRepo, timeout)

  def apply[E, ID](asyncRepo : AsyncCrudRepo[E, ID], timeout: Duration) : SyncCrudRepo[E, ID] =
    new SyncCrudRepoAdapter(asyncRepo, timeout)

  def apply[E, ID](asyncRepo : AsyncStreamRepo[E, ID], timeout: Duration) : SyncStreamRepo[E, ID] =
    new SyncStreamRepoAdapter(asyncRepo, timeout)
}

// Adapters

private class SyncReadonlyRepoAdapter[E, ID](
  asyncRepo : AsyncReadonlyRepo[E, ID],
  timeout : Duration
) extends SyncReadonlyRepo[E, ID] {

  override def get(id: ID): Option[E] =
    wait(asyncRepo.get(id))

  override def find(
    criteria: Seq[Criterion[Any]] = Nil,
    sort: Seq[Sort] = Nil,
    projection : Traversable[String] = Nil,
    limit: Option[Int] = None,
    skip: Option[Int] = None
  ) =
    wait(asyncRepo.find(criteria, sort, projection, limit, skip))

  override def count(criteria: Seq[Criterion[Any]]) =
    wait(asyncRepo.count(criteria))

  protected def wait[T](future : Awaitable[T]): T =
    Await.result(future, timeout)
}

private class SyncRepoAdapter[E, ID](
  asyncRepo : AsyncRepo[E, ID],
  timeout : Duration
) extends SyncReadonlyRepoAdapter[E, ID](asyncRepo, timeout) with SyncRepo[E, ID] {

  override def save(entity: E) =
    wait(asyncRepo.save(entity))

  override def save(entities: Traversable[E]) =
    wait(asyncRepo.save(entities))

  override def flushOps =
    wait(asyncRepo.flushOps)
}

private class SyncCrudRepoAdapter[E, ID](
  asyncRepo : AsyncCrudRepo[E, ID],
  timeout : Duration
) extends SyncRepoAdapter[E, ID](asyncRepo, timeout) with SyncCrudRepo[E, ID] with Serializable {

  override def update(entity: E) =
    wait(asyncRepo.update(entity))

  override def update(entities: Traversable[E]) =
    wait(asyncRepo.update(entities))

  override def delete(id: ID) =
    wait(asyncRepo.delete(id))

  override def delete(ids: Traversable[ID]) =
    wait(asyncRepo.delete(ids))

  override def deleteAll =
    wait(asyncRepo.deleteAll)
}

private class SyncStreamRepoAdapter[E, ID](
  asyncRepo : AsyncStreamRepo[E, ID],
  timeout : Duration
) extends SyncRepoAdapter[E, ID](asyncRepo, timeout) with SyncStreamRepo[E, ID] {

  override def stream = asyncRepo.stream
}