package models

import play.api.libs.json.{JsObject, JsValue, Json, OFormat, Reads}

import java.util.UUID
import java.util.UUID.randomUUID
import java.util.Calendar
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

case class TimeInput(
  id: UUID = UUID.randomUUID(),
  input: Long = 0,
  project: Project = Project(
    id = UUID.fromString("44e4653d-7f71-4cf2-90f3-804f949ba264"),
    name = "Dummy",
    description = "This is a dummy project.",
    owner = User.dummyManager,
    creator = User.dummyManager,
    managers = List(User.dummyManager, User.dummyManager2),
    client = Client(randomUUID(), "client " + Clock.systemUTC().instant(), "some@email.invalid", Calendar.getInstance().getTimeInMillis, Calendar.getInstance().getTimeInMillis),
    billable = false,
    employees = List(User.dummyEmployee, User.dummyEmployee2),
    tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000010L,
    lastEditor = User.dummyManager
  ),
  employee: User = User.dummyEmployee,
  date: LocalDate = LocalDate.now(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis(),
  description: String = "This timeinput was created on " + LocalDateTime.now()
//  billed: Boolean = false,
//  confirmed: Boolean = false
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
