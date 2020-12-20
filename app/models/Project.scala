package models

import play.api.libs.json.{Json, OFormat, OWrites, Reads, JsValue, _}

import scala.collection.mutable.ArrayBuffer

case class Project(
  id: Long = Project.all.maxBy(_.id).id + 1,
  name: String = "",
  description: String = "",
  owner: User = User.dummyManager,
  creator: User = User.dummyManager,
  managers: List[User] = List(User.dummyManager),
  client: String = "",
  billable: Boolean = true,
  employees: List[User] = List(),
  tags: List[String] = List(),
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis(),
  lastEditor: User = User.dummyManager
)

object Project {

  val dummy: Project =
    Project(
      id = 1000,
      name = "Dummy",
      description = "This is a dummy project.",
      owner = User.dummyManager,
      creator = User.dummyManager,
      managers = List(User.dummyManager, User.dummyManager2),
      client = "Some client",
      billable = false,
      employees = List(User.dummyEmployee, User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager
    )

  val dummy2: Project =
    Project(
      id = 1001,
      name = "Another dummy",
      description = "This is another dummy project.",
      owner = User.dummyManager2,
      creator = User.dummyManager2,
      managers = List(User.dummyManager2),
      client = "Some client",
      billable = true,
      employees = List(User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 200000000000L,
      lastEdited = 200000000030L,
      lastEditor = User.dummyManager2
    )

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  val all: ArrayBuffer[Project]   = ArrayBuffer(dummy, dummy2)
  def add(project: Project): Unit = all append project
}

case class AddProjectDTO(
  name: String,
  description: String,
  client: String,
  owner: Long,
  billable: Boolean
) {

  def asProject: Project =
    Project(
      name = this.name,
      description = this.description,
      client = this.client,
      owner = User.byId(this.owner),
      creator = User.byId(this.owner),
      managers = List(User.byId(this.owner)),
      lastEditor = User.byId(this.owner),
      billable = this.billable
    )
}
object AddProjectDTO {
  implicit val readProjectDTO: Reads[AddProjectDTO] = Json.reads[AddProjectDTO]
}
