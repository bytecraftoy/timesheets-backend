package models

import play.api.libs.json.{Json, OFormat, OWrites, Reads}

import scala.collection.mutable.ArrayBuffer

case class Employee(
  id: Int = Employee.all.maxBy(_.id).id + 1,
  username: String = "user" + (Employee.all.maxBy(_.id).id + 1),
  firstName: String = "Firstname",
  lastName: String = "Lastname"
)

object Employee {
  val dummyManager: Employee =
    Employee(
      id = 1,
      username = "some.manager@gmail.com",
      firstName = "Some",
      lastName = "Manager"
    )
  val dummyManager2: Employee =
    Employee(
      id = 2,
      username = "another.manager@gmail.com",
      firstName = "Another",
      lastName = "Manager"
    )
  val dummyEmployee: Employee =
    Employee(
      id = 3,
      username = "some.developer@gmail.com",
      firstName = "Some",
      lastName = "Developer"
    )
  val dummyEmployee2: Employee =
    Employee(
      id = 4,
      username = "another.developer@gmail.com",
      firstName = "Another",
      lastName = "Developer"
    )

  implicit def apply(i: Int): Employee = Employee.all.filter(_.id == i).head

  implicit def employeeFormat: OFormat[Employee] =
    Json.using[Json.WithDefaultValues].format[Employee]

  val all: ArrayBuffer[Employee] =
    ArrayBuffer(dummyManager, dummyManager2, dummyEmployee, dummyEmployee2)
  def add(employee: Employee): Unit = all append employee
}
