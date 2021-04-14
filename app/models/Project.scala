package models

import play.api.libs.json.{Json, OFormat}

import java.util.UUID
import java.util.UUID.randomUUID

case class Project(
  id: UUID = randomUUID(),
  name: String = "",
  description: String = "",
  owner: User,
  creator: User,
  managers: List[User] = List(),
  client: Client,
  billable: Boolean = true,
  employees: List[User] = List(),
  tags: List[String] = List(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis(),
  lastEditor: User
) {
  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]
}
