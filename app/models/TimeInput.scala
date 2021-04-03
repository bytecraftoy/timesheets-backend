package models

import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate
import java.util.UUID

case class TimeInput(
  id: UUID = UUID.randomUUID(),
  employee: User = null,
  project: Project = null,
  date: LocalDate = null,
  input: Long = 0,
  description: String = null,
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis()
) {
  def compactJson: JsValue =
    Json.obj(
      "id"                -> id,
      "input"             -> input,
      "date"              -> date,
      "creationTimestamp" -> creationTimestamp,
      "lastEdited"        -> lastEdited,
      "description"       -> description
    )
}
