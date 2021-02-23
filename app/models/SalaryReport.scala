package models

import java.time.LocalDate
import java.util.UUID

case class SalaryReport(
  startDate: LocalDate,
  endDate: LocalDate,
  created: Long = System.currentTimeMillis(),
  employee: User,
  grandTotal: Long, // inputted minutes
  clients: List[SimpleClient]
)

case class SimpleClient(
  id: UUID,
  name: String,
  clientTotal: Long, // inputted minutes
  projects: List[SimpleProject]
)

case class SimpleTimeInput(
  id: UUID,
  description: String,
  input: Long,
  created: Long,
  updated: Long,
  date: LocalDate
)

case class SimpleProject(
  id: UUID,
  name: String,
  projectTotal: Long, // inputted minutes
  timeInputs: List[SimpleTimeInput]
)
