package domain.models

import play.api.libs.json.{Json, OFormat}

import java.util.UUID
import java.util.UUID.randomUUID

case class Project(
  id: UUID = randomUUID(),
  name: String = "",
  description: String = "",
  owner: User,
  createdBy: User,
  managers: List[User] = List(),
  client: Client,
  billable: Boolean = true,
  employees: List[User] = List(),
  tags: List[String] = List(),
  created: Long = System.currentTimeMillis(),
  edited: Long = System.currentTimeMillis(),
  editedBy: User,
  hourlyCost: HourlyCost = HourlyCost(value = BigDecimal(0), currency = "EUR")
) {
  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]
}
