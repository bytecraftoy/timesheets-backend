package web.dto

import domain.models.Client
import play.api.libs.json.{Json, OFormat}
import io.swagger.annotations.ApiModelProperty

import java.util.UUID

case class ClientDTO(
  @ApiModelProperty(value = "UUID of the client")
  id: UUID,
  @ApiModelProperty(example = "Company")
  name: String,
  @ApiModelProperty(example = "example@company.com")
  email: String,
  @ApiModelProperty(value = "Creation time in UTC milliseconds")
  created: Long,
  @ApiModelProperty(value = "Time of last edit in UTC milliseconds")
  edited: Long
)

object ClientDTO {

  def fromDomain(client: Client): ClientDTO =
    ClientDTO(
      id = client.id,
      name = client.name,
      email = client.email,
      created = client.created,
      edited = client.edited
    )

  implicit def clientDTOFormat: OFormat[ClientDTO] =
    Json.using[Json.WithDefaultValues].format[ClientDTO]
}

case class AddClientDTO(name: String, email: String)

object AddClientDTO {

  def toDomain(addClientDTO: AddClientDTO): Client =
    Client(name = addClientDTO.name, email = addClientDTO.email)

  implicit def addClientDTOFormat: OFormat[AddClientDTO] =
    Json.using[Json.WithDefaultValues].format[AddClientDTO]
}
