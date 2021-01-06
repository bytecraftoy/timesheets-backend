package models
import com.google.inject.ImplementedBy
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[DevelopmentTimeInputRepository])
trait TimeInputRepository extends Repository[TimeInput] {

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput]

  def jsonByProject(
    i: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsValue

  def jsonGroupedByProject(
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsObject

}

class DevelopmentTimeInputRepository @Inject() (
  projectRepository: ProjectRepository
)(implicit executionContext: ExecutionContext)
    extends TimeInputRepository {

  val input1: TimeInput = TimeInput(
    id = UUID.fromString("9147e577-7303-4c59-9d77-8d1216968646"),
    input = 450,
    project = Project(
      id = UUID.fromString("44e4653d-7f71-4cf2-90f3-804f949ba264"),
      name = "Dummy",
      description = "This is a dummy project.",
      owner = User.dummyManager,
      creator = User.dummyManager,
      managers = List(User.dummyManager, User.dummyManager2),
      client = Client.client1,
      billable = false,
      employees = List(User.dummyEmployee, User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager
    ),
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-16"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input2: TimeInput = TimeInput(
    id = UUID.fromString("0f5e6551-ebf0-46bd-ad17-bc796043a25c"),
    input = 450,
    project = Project(
      id = UUID.fromString("44e4653d-7f71-4cf2-90f3-804f949ba264"),
      name = "Dummy",
      description = "This is a dummy project.",
      owner = User.dummyManager,
      creator = User.dummyManager,
      managers = List(User.dummyManager, User.dummyManager2),
      client = Client.client1,
      billable = false,
      employees = List(User.dummyEmployee, User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager
    ),
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-17"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input3: TimeInput = TimeInput(
    id = UUID.fromString("5f37bc60-3f7a-436a-a238-2b4f908fd235"),
    input = 450,
    project = Project(
      id = UUID.fromString("44e4653d-7f71-4cf2-90f3-804f949ba264"),
      name = "Dummy",
      description = "This is a dummy project.",
      owner = User.dummyManager,
      creator = User.dummyManager,
      managers = List(User.dummyManager, User.dummyManager2),
      client = Client.client1,
      billable = false,
      employees = List(User.dummyEmployee, User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager
    ),
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-18"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input4: TimeInput = TimeInput(
    id = UUID.fromString("6122d110-bf62-4d7b-b896-d3350363e256"),
    input = 450,
    project = Project(
      id = UUID.fromString("6ffdeea7-c6bd-42e5-a5a3-49526cbd001a"),
      name = "Another dummy",
      description = "This is another dummy project.",
      owner = User.dummyManager2,
      creator = User.dummyManager2,
      managers = List(User.dummyManager2),
      client = Client.client3,
      billable = false,
      employees = List(User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager2
    ),
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-19"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )
  val input5: TimeInput = TimeInput(
    id = UUID.fromString("17f12c6b-4149-4d45-8dee-741147a930aa"),
    input = 450,
    project = Project(
      id = UUID.fromString("6ffdeea7-c6bd-42e5-a5a3-49526cbd001a"),
      name = "Another dummy",
      description = "This is another dummy project.",
      owner = User.dummyManager2,
      creator = User.dummyManager2,
      managers = List(User.dummyManager2),
      client = Client.client3,
      billable = false,
      employees = List(User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager2
    ),
    employee = User.dummyEmployee,
    date = LocalDate.parse("2020-11-20"),
    creationTimestamp = 100000000000L,
    lastEdited = 100000000000L
  )

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  implicit def timeInputFormat: OFormat[TimeInput] = {
    Json.using[Json.WithDefaultValues].format[TimeInput]
  }

  val timeInputInMemory: ArrayBuffer[TimeInput] =
    ArrayBuffer(input1, input2, input3, input4, input5)

  def all: Seq[TimeInput] = timeInputInMemory.toSeq

  override def byId(id: UUID): TimeInput = all.filter(_.id == id).head

  def byProject(i: UUID): Seq[TimeInput] =
    all.filter(_.project.id == i)

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput] =
    all.filter(x => x.date.isAfter(start) && x.date.isBefore(end))

  def add(timeInput: TimeInput): Unit = timeInputInMemory append timeInput

  def jsonByProject(
    i: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsValue =
    Json.toJson(
      all
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
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsObject =
    Json.obj(
      "id"       -> employeeId,
      "username" -> User.byId(employeeId).username,
      "projects" -> all
        .filter(
          timeInput =>
            (timeInput.date.isAfter(start) || timeInput.date.isEqual(start))
              && (timeInput.date.isBefore(end) || timeInput.date.isEqual(end))
              && timeInput.employee.id == employeeId
        )
        .groupBy(_.project.id)
        .map(
          projectIdToTimeInputs =>
            Json.obj(
              "id"    -> projectIdToTimeInputs._1,
              "name"  -> projectRepository.byId(projectIdToTimeInputs._1).name,
              "hours" -> projectIdToTimeInputs._2.map(_.compactJson)
            )
        )
    )
}
