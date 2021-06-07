package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import java.util.UUID

case class TimeInput(
  id: UUID = UUID.randomUUID(),
  employee: User,
  project: Project,
  date: LocalDate,
  input: Long = 0,
  description: String,
  created: Long = System.currentTimeMillis(),
  edited: Long = System.currentTimeMillis()
)

object TimeInput {
  implicit def timeInputFormat: OFormat[TimeInput] =
    Json.using[Json.WithDefaultValues].format[TimeInput]
}
