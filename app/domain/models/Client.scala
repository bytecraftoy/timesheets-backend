package domain.models

import anorm.{Macro, ToParameterList}
import play.api.libs.json.{Json, OFormat}

import java.time.Clock
import java.util.UUID.randomUUID
import java.util.{Calendar, UUID}

case class Client(
  id: UUID = randomUUID(),
  name: String = "client " + Clock.systemUTC().instant(),
  email: String = "",
  created: Long = Calendar.getInstance().getTimeInMillis,
  edited: Long = Calendar.getInstance().getTimeInMillis
)

object Client {
  implicit def clientFormat: OFormat[Client] =
    Json.using[Json.WithDefaultValues].format[Client]

  implicit def toParameters: ToParameterList[Client] =
    Macro.toParameters[Client]
}
