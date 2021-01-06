package models

import play.api.libs.json.{JsObject, JsValue, Json, OFormat, Reads}

import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer

case class TimeInput(
  id: Long = TimeInput.all.maxBy(_.id).id + 1,
  input: Long = 0,
  project: Project = Project.dummy,
  employee: User = User.dummyEmployee,
  date: LocalDate = LocalDate.now(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis()
//  billed: Boolean = false,
//  confirmed: Boolean = false
) {
  def compactJson: JsValue =
    Json.obj(
      "id"                -> id,
      "input"             -> input,
      "date"              -> date,
      "creationTimestamp" -> creationTimestamp,
      "lastEdited"        -> lastEdited
    )
}
object TimeInput {
  val input1: TimeInput = TimeInput(
    id = 1,
    input = 450,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-12-16"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input2: TimeInput = TimeInput(
    id = 2,
    input = 450,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-12-17"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input3: TimeInput = TimeInput(
    id = 3,
    input = 450,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-12-18"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input4: TimeInput = TimeInput(
    id = 4,
    input = 450,
    project = Project.dummy2,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-12-28"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input5: TimeInput = TimeInput(
    id = 5,
    input = 450,
    project = Project.dummy2,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-12-30"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )

  implicit def timeInputFormat: OFormat[TimeInput] =
    Json.using[Json.WithDefaultValues].format[TimeInput]

  val all: ArrayBuffer[TimeInput] =
    ArrayBuffer(input1, input2, input3, input4, input5)

  def byProject(i: Long): ArrayBuffer[TimeInput] =
    TimeInput.all.filter(_.project.id == i)

  def byTimeInterval(start: LocalDate, end: LocalDate): ArrayBuffer[TimeInput] =
    TimeInput.all.filter(x => x.date.isAfter(start) && x.date.isBefore(end))

  def add(timeInput: TimeInput): Unit = all append timeInput

  def jsonByProject(
    i: Long,
    employeeId: Long,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsValue =
    Json.toJson(
      TimeInput.all
        .filter(
          t =>
            (t.date.isAfter(start) || t.date.isEqual(start))
              && (t.date.isBefore(end) || t.date.isEqual(end))
              && t.employee.id == employeeId && t.project.id == i
        )
        .map(
          ti =>
            Json.obj(
              "id"                -> ti.id,
              "input"             -> ti.input,
              "date"              -> ti.date,
              "creationTimestamp" -> ti.creationTimestamp,
              "lastEdited"        -> ti.lastEdited
            )
        )
    )

  def jsonGroupedByProject(
    employeeId: Long,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsObject =
    Json.obj(
      "id"       -> employeeId,
      "username" -> User.byId(employeeId).username,
      "projects" -> TimeInput.all
        .filter(
          t =>
            (t.date.isAfter(start) || t.date.isEqual(start))
              && (t.date.isBefore(end) || t.date.isEqual(end))
              && t.employee.id == employeeId
        )
        .groupBy(_.project.id)
        .map(
          kv =>
            Json.obj(
              "id"    -> kv._1,
              "name"  -> Project.byId(kv._1).name,
              "hours" -> kv._2.map(_.compactJson)
            )
        )
    )
}

case class AddTimeInputDTO(
  input: Long,
  project: Long,
  employee: Long,
  date: String
) {
  def asTimeInput: TimeInput = {
    TimeInput(
      input = this.input,
      project = Project.byId(this.project),
      employee = User.byId(this.employee),
      date =
        LocalDate.parse(
          this.date
        ) // dateInput must be a String in format "yyyy-MM-dd"
    )
  }
}

object AddTimeInputDTO {
  implicit val readTimeInputDTO: Reads[AddTimeInputDTO] =
    Json.reads[AddTimeInputDTO]
}
