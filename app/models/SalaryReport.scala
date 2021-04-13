package models

import io.swagger.annotations.ApiModelProperty

import java.time.LocalDate
import java.util.UUID

case class SalaryReport(
  startDate: LocalDate,
  endDate: LocalDate,
  created: Long = System.currentTimeMillis(),
  employee: User,
  @ApiModelProperty(value = "Inputted minutes")
  grandTotal: Long,
  grandTotalCost: HourlyCost,
  billable: Boolean,
  nonBillable: Boolean,
  clients: List[SimpleClient]
)

case class SimpleClient(
  id: UUID,
  name: String,
  @ApiModelProperty(value = "Inputted minutes")
  clientTotal: Long,
  clientTotalCost: HourlyCost,
  projects: List[SimpleProject]
)

case class SimpleTimeInput(
  id: UUID,
  description: String,
  input: Long,
  cost: HourlyCost,
  created: Long,
  updated: Long,
  date: LocalDate
)

case class SimpleProject(
  id: UUID,
  name: String,
  @ApiModelProperty(value = "Inputted minutes")
  projectTotal: Long,
  projectTotalCost: HourlyCost,
  billable: Boolean,
  timeInputs: List[SimpleTimeInput]
)
