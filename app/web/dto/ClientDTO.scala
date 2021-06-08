package web.dto

import play.api.libs.json.{Json, OFormat}

case class AddClientDTO(name: String, email: String)
object AddClientDTO {
  implicit def addClientDTOFormat: OFormat[AddClientDTO] =
    Json.using[Json.WithDefaultValues].format[AddClientDTO]
}
