package models

import anorm.{Macro, ToParameterList}
import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.{Json, OFormat}

import java.time.Clock
import java.util.UUID.randomUUID
import java.util.{Calendar, UUID}

case class Client(
  @ApiModelProperty(value = "UUID of the client")
  id: UUID = randomUUID(),
  @ApiModelProperty(example = "Company")
  name: String = "client " + Clock.systemUTC().instant(),
  @ApiModelProperty(example = "example@company.com")
  email: String = "",
  @ApiModelProperty(value = "Creation time in UTC milliseconds")
  timestamp_created: Long = Calendar.getInstance().getTimeInMillis,
  @ApiModelProperty(value = "Time of last edit in UTC milliseconds")
  timestamp_edited: Long = Calendar.getInstance().getTimeInMillis
)

object Client {
  implicit def ClientFormat: OFormat[Client] =
    Json.using[Json.WithDefaultValues].format[Client]

  implicit def toParameters: ToParameterList[Client] =
    Macro.toParameters[Client]
}
