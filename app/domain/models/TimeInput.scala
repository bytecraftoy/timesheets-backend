package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import java.util.UUID

case class TimeInput(
  id: UUID = UUID.randomUUID(),
  employee: User = null,
  project: Project = null,
  date: LocalDate = null,
  input: Long = 0,
  description: String = null,
  created: Long = System.currentTimeMillis(),
  edited: Long = System.currentTimeMillis()
)

object TimeInput {
  implicit def timeInputFormat: OFormat[TimeInput] =
    Json.using[Json.WithDefaultValues].format[TimeInput]
}
