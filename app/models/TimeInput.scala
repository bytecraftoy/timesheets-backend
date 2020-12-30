package models

import java.time.LocalDate

import play.api.libs.json.{Json, OFormat, Reads}

import scala.collection.mutable.ArrayBuffer

case class TimeInput(
  id: Long = TimeInput.all.maxBy(_.id).id + 1,
  input: BigDecimal = 0,
  project: Project = Project.dummy,
  employee: User = User.dummyEmployee,
  date: LocalDate = LocalDate.now(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis()
//  billed: Boolean = false,
//  confirmed: Boolean = false
)
object TimeInput {
  val input1: TimeInput = TimeInput(
    id = 1,
    input = 7.5,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-16"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input2: TimeInput = TimeInput(
    id = 2,
    input = 7.5,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-17"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input3: TimeInput = TimeInput(
    id = 3,
    input = 7.5,
    project = Project.dummy,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-18"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input4: TimeInput = TimeInput(
    id = 4,
    input = 7.5,
    project = Project.dummy2,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-19"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input5: TimeInput = TimeInput(
    id = 5,
    input = 7.5,
    project = Project.dummy2,
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-20"),
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
    TimeInput.all.filter(x => (x.date.isAfter(start) && x.date.isBefore(end)))

  def add(timeInput: TimeInput): Unit = all append timeInput
}

case class AddTimeInputDTO(
  input: BigDecimal,
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
