package models

import anorm.{Macro, ToParameterList}
import play.api.libs.json.{JsObject, JsValue, Json, OFormat, Reads}

import java.util.UUID
import java.time.LocalDate

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
