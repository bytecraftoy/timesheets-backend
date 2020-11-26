package models

import play.api.libs.json.{Json, OWrites, Reads}
import scala.collection.mutable.ArrayBuffer

case class Project(
  id: Int = Project.all.maxBy(_.id).id + 1,
  name: String = "",
  description: String = "",
  owner: Employee = Employee.dummyManager,
  creator: Employee = Employee.dummyManager,
  managers: List[Employee] = List(Employee.dummyManager),
  client: String = "",
  billable: Boolean = true,
  employees: List[Employee] = List(),
  tags: List[String] = List(),
  creationTimestamp: Long = System.currentTimeMillis() / 1000L,
  lastEdited: Long = System.currentTimeMillis() / 1000L,
  lastEditor: Employee = Employee.dummyManager
)

object Project {

  val dummy: Project =
    Project(
      id = 1000,
      name = "Dummy",
      description = "This is a dummy project.",
      owner = Employee.dummyManager,
      creator = Employee.dummyManager,
      managers = List(Employee.dummyManager, Employee.dummyManager2),
      client = "Some client",
      billable = false,
      employees = List(Employee.dummyEmployee, Employee.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = Employee.dummyManager
    )

  val dummy2: Project =
    Project(
      id = 1001,
      name = "Another dummy",
      description = "This is another dummy project.",
      owner = Employee.dummyManager2,
      creator = Employee.dummyManager2,
      managers = List(Employee.dummyManager2),
      client = "Some client",
      billable = true,
      employees = List(Employee.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 200000000000L,
      lastEdited = 200000000030L,
      lastEditor = Employee.dummyManager2
    )

  implicit val readProject: Reads[Project] = Json.reads[Project]

  implicit val writeProject: OWrites[Project] = Json.writes[Project]

  val all: ArrayBuffer[Project]   = ArrayBuffer(dummy, dummy2)
  def add(project: Project): Unit = all append project
}
