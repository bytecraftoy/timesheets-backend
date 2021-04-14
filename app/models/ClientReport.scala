package models

import java.time.LocalDate
import java.util.UUID

case class ClientReport(
  startDate: LocalDate,
  endDate: LocalDate,
  creationMillis: Long = System.currentTimeMillis(),
  client: Client,
  projects: List[ProjectSimple],
  grandTotal: Long,
  billable: Boolean,
  nonBillable: Boolean
)

case class ProjectSimple(
  id: UUID,
  name: String,
  projectTotal: Long,
  billable: Boolean,
  employees: List[EmployeeSimple]
)

case class EmployeeSimple(
  id: UUID,
  firstName: String,
  lastName: String,
  employeeTotal: Long,
  timeInputs: List[TimeInputSimple]
)

case class TimeInputSimple(
  id: UUID,
  date: LocalDate,
  input: Long,
  description: String
)
