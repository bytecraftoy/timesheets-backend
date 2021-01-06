package models

import play.api.libs.json.{Json, OFormat, OWrites, Reads}

import java.util.UUID
import scala.collection.mutable.ArrayBuffer

case class User(
  id: UUID = UUID.randomUUID(),
  username: String = "user " + System.currentTimeMillis(),
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
      id = UUID.fromString("f8396bc3-6933-4418-9750-774efc8b49e2"),
      username = "some.manager@gmail.com",
      firstName = "Some",
      lastName = "Manager"
    )
  val dummyManager2: User =
    User(
      id = UUID.fromString("f7a0112b-6d46-41aa-aa2f-fffd73b4946f"),
      username = "another.manager@gmail.com",
      firstName = "Another",
      lastName = "Manager"
    )
  val dummyEmployee: User =
    User(
      id = UUID.fromString("a3f4e844-4199-439d-a463-2f07e87c6ca4"),
      username = "some.developer@gmail.com",
      firstName = "Some",
      lastName = "Developer"
    )
  val dummyEmployee2: User =
    User(
      id = UUID.fromString("fc43f847-7d3a-4f59-897d-6a01757cae17"),
      username = "another.developer@gmail.com",
      firstName = "Another",
      lastName = "Developer"
    )

  val dbManager1: User =
    User(
      id = UUID.fromString("9fa407f4-7375-446b-92c6-c578839b7780"),
      username = "ekakäyttäjä",
      firstName = "Eka",
      lastName = "E_sukunimi"
    )

  val dbManager2: User =
    User(
      id = UUID.fromString("06be4b85-8f65-4f65-8965-faba1216f199"),
      username = "tokakäyttäjä",
      firstName = "Toka",
      lastName = "T_sukunimi"
    )

  def byId(i: UUID): User = User.all.filter(_.id == i).head

  implicit def employeeFormat: OFormat[User] =
    Json.using[Json.WithDefaultValues].format[User]

  val all: ArrayBuffer[User] =
    ArrayBuffer(
      dummyManager,
      dummyManager2,
      dummyEmployee,
      dummyEmployee2,
      dbManager1,
      dbManager2
    )
  def add(employee: User): Unit = all append employee
}
