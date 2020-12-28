package models

import play.api.libs.json.{Json, OFormat, OWrites, Reads}

import scala.collection.mutable.ArrayBuffer

case class User(
  id: Long = User.all.maxBy(_.id).id + 1,
  username: String = "user" + (User.all.maxBy(_.id).id + 1),
  firstName: String = "Firstname",
  lastName: String = "Lastname",
  email: String = "firstname.lastname@gmail.com",
  phoneNumber: String = "0123456789",
  salary: BigDecimal = 0,
  isManager: Boolean = false,
  creationTimestamp: Long = System.currentTimeMillis(),
  lastEdited: Long = System.currentTimeMillis()
)

object User {
  val dummyManager: User =
    User(
      id = 1,
      username = "some.manager@gmail.com",
      firstName = "Some",
      lastName = "Manager"
    )
  val dummyManager2: User =
    User(
      id = 2,
      username = "another.manager@gmail.com",
      firstName = "Another",
      lastName = "Manager"
    )
  val dummyEmployee: User =
    User(
      id = 3,
      username = "some.developer@gmail.com",
      firstName = "Some",
      lastName = "Developer"
    )
  val dummyEmployee2: User =
    User(
      id = 4,
      username = "another.developer@gmail.com",
      firstName = "Another",
      lastName = "Developer"
    )

  def byId(i: Long): User = User.all.filter(_.id == i).head

  implicit def employeeFormat: OFormat[User] =
    Json.using[Json.WithDefaultValues].format[User]

  val all: ArrayBuffer[User] =
    ArrayBuffer(dummyManager, dummyManager2, dummyEmployee, dummyEmployee2)
  def add(employee: User): Unit = all append employee
}
