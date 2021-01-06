package models

import java.util.UUID

trait Repository[T] {
  def byId(id: UUID): T
  def all: Seq[T]
  def add(t: T): Unit
}
