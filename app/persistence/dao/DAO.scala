package persistence.dao

import java.util.UUID

trait DAO[T] {
  def getAll: Seq[T]
  def getById(id: UUID): Option[T]
  def add(t: T): Unit
}
