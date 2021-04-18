package models

import java.time.LocalDate
import java.util.UUID

case class SalaryReport(
  startDate: LocalDate,
  endDate: LocalDate,
  created: Long = System.currentTimeMillis(),
  employee: User,
  grandTotal: Long, // inputted minutes
  grandTotalCost: HourlyCost,
  billable: Boolean,
  nonBillable: Boolean,
  clients: List[SimpleClient]
)

case class SimpleClient(
  id: UUID,
  name: String,
  clientTotal: Long, // inputted minutes
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
  projectTotal: Long, // inputted minutes
  projectTotalCost: HourlyCost,
  billable: Boolean,
  timeInputs: List[SimpleTimeInput]
)
