package models

import play.api.libs.json.{Json, OFormat}

import java.util.UUID.randomUUID
import java.util.{Calendar, UUID}

import java.time.Clock

case class Project(
  id: UUID = randomUUID(),
  name: String = "",
  description: String = "",
  owner: User = User.dummyManager,
  creator: User = User.dummyManager,
  managers: List[User] = List(),
  client: Client = Client(
    randomUUID(),
    "client " + Clock.systemUTC().instant(),
    "some@email.invalid",
    Calendar.getInstance().getTimeInMillis,
    Calendar.getInstance().getTimeInMillis
  ),
  billable: Boolean = true,
  employees: List[User] = List(),
  tags: List[String] = List(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis(),
  lastEditor: User = User.dummyManager2
) {
  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]
}
