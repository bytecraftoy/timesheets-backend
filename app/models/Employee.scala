package models

import play.api.libs.json.{Json, OWrites, Reads}
import scala.collection.mutable.ArrayBuffer

case class Employee(id: Int, firstName: String, lastName: String)

object Employee {
  val dummyManager: Employee =
    Employee(id = 1, firstName = "Some", lastName = "Manager")
  val dummyManager2: Employee =
    Employee(id = 2, firstName = "Another", lastName = "Manager")
  val dummyEmployee: Employee =
    Employee(id = 3, firstName = "Some", lastName = "Developer")
  val dummyEmployee2: Employee =
    Employee(id = 4, firstName = "Another", lastName = "Developer")

  implicit val readEmployee: Reads[Employee] = Json.reads[Employee]

  implicit val writeEmployee: OWrites[Employee] = Json.writes[Employee]

  val all: ArrayBuffer[Employee] =
    ArrayBuffer(dummyManager, dummyManager2, dummyEmployee, dummyEmployee2)
  def add(employee: Employee): Unit = all append employee
}
