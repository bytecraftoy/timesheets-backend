package models

import anorm.{Macro, ToParameterList}
import play.api.libs.json.{Json, OFormat}

import java.util.UUID

case class User(
  id: UUID = UUID.randomUUID(),
  username: String = "user " + System.currentTimeMillis(),
  firstName: String = "Firstname",
  lastName: String = "Lastname",
  email: String = "firstname.lastname@gmail.com",
  phoneNumber: String = "0123456789",
  salary: BigDecimal = 0,
  isManager: Boolean = false,
  created: Long = System.currentTimeMillis(),
  edited: Long = System.currentTimeMillis()
)

object User {

  implicit def userFormat: OFormat[User] =
    Json.using[Json.WithDefaultValues].format[User]

  implicit def toParameters: ToParameterList[User] =
    Macro.toParameters[User]

}
