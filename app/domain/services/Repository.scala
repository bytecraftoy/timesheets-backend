package domain.services

import java.util.UUID

trait Repository[T] {
  def byId(id: UUID): Option[T]
  def all: Seq[T]
  def add(t: T): Unit
}
