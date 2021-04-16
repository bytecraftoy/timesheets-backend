package dto

import io.swagger.annotations.ApiModelProperty
import models.HourlyCost
import play.api.libs.json.{Json, OFormat, Reads}

import java.util.UUID

case class AddProjectDTO(
  name: String,
  description: String,
  client: UUID,
  owner: UUID,
  billable: Boolean,
  employees: List[UUID],
  hourlyCost: HourlyCost = HourlyCost(0, "EUR")
)
object AddProjectDTO {
  implicit def addProjectDTOFormat: OFormat[AddProjectDTO] =
    Json.using[Json.WithDefaultValues].format[AddProjectDTO]
}
case class UpdateProjectDTO(
  @ApiModelProperty(value = "UUID of an existing project")
  id: UUID,
  name: String,
  description: String,
  client: UUID,
  owner: UUID,
  billable: Boolean,
  employees: List[UUID],
  hourlyCost: HourlyCost = HourlyCost(0, "EUR")
)
object UpdateProjectDTO {
  implicit def updateProjectDTOFormat: OFormat[UpdateProjectDTO] =
    Json.using[Json.WithDefaultValues].format[UpdateProjectDTO]
}
