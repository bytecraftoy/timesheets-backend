package domain.models

import java.time.LocalDate
import java.util.UUID

case class ClientReport(
  startDate: LocalDate,
  endDate: LocalDate,
  created: Long = System.currentTimeMillis(),
  client: Client,
  projects: List[ProjectSimple],
  grandTotal: Long,
  grandTotalCost: HourlyCost,
  billable: Boolean,
  nonBillable: Boolean
)

case class ProjectSimple(
  id: UUID,
  name: String,
  projectTotal: Long,
  projectTotalCost: HourlyCost,
  billable: Boolean,
  employees: List[EmployeeSimple]
)

case class EmployeeSimple(
  id: UUID,
  firstName: String,
  lastName: String,
  employeeTotal: Long,
  employeeTotalCost: HourlyCost,
  timeInputs: List[TimeInputSimple]
)

case class TimeInputSimple(
  id: UUID,
  date: LocalDate,
  input: Long,
  cost: HourlyCost,
  description: String
)
