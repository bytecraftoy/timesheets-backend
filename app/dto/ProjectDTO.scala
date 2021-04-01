package dto

import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.{Json, Reads}

import java.util.UUID

case class AddProjectDTO(
  name: String,
  description: String,
  client: UUID,
  owner: UUID,
  billable: Boolean,
  employees: List[UUID]
)
object AddProjectDTO {
  implicit val readProjectDTO: Reads[AddProjectDTO] =
    Json.reads[AddProjectDTO]
}
case class UpdateProjectDTO(
  @ApiModelProperty(value = "UUID of an existing project")
  id: UUID,
  name: String,
  description: String,
  client: UUID,
  owner: UUID,
  billable: Boolean,
  employees: List[UUID]
)
object UpdateProjectDTO {
  implicit val readProjectDTO: Reads[UpdateProjectDTO] =
    Json.reads[UpdateProjectDTO]
}
