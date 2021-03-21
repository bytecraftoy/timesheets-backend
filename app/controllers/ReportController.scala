package controllers

import io.swagger.annotations.Api
import models.{
  ClientReport,
  ClientReportService,
  EmployeeSimple,
  ProjectSimple,
  SalaryReport,
  SalaryReportService,
  SimpleClient,
  SimpleProject,
  SimpleTimeInput,
  TimeInput,
  TimeInputSimple
}
import play.api.Logging
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@Api
class ReportController @Inject() (
  cc: ControllerComponents,
  clientReportService: ClientReportService,
  salaryReportService: SalaryReportService
) extends AbstractController(cc)
    with Logging {

  implicit val TimeInputSimpleWrites: OWrites[TimeInputSimple] =
    Json.writes[TimeInputSimple]
  implicit val employeeSimpleWrites: OWrites[EmployeeSimple] =
    Json.writes[EmployeeSimple]
  implicit val projectSimpleWrites: OWrites[ProjectSimple] =
    Json.writes[ProjectSimple]
  implicit val clientReportWrites: OWrites[ClientReport] =
    Json.writes[ClientReport]

  implicit val simpleTimeInputWrites: OWrites[SimpleTimeInput] =
    Json.writes[SimpleTimeInput]
  implicit val simpleProjectWrites: OWrites[SimpleProject] =
    Json.writes[SimpleProject]
  implicit val simpleClientWrites: OWrites[SimpleClient] =
    Json.writes[SimpleClient]
  implicit val salaryReportWrites: OWrites[SalaryReport] =
    Json.writes[SalaryReport]

  def getClientReport(
    clientIdString: String,
    projectIdStringList: List[String],
    startDateString: String,
    endDateString: String,
    employeeIdStringList: List[String],
    billable: Boolean,
    nonBillable: Boolean
  ): Action[AnyContent] =
    Action {
      try {

        val projectUuids: List[UUID] =
          projectIdStringList.map(idString => UUID.fromString(idString))
        val employeeUuids: List[UUID] =
          employeeIdStringList.map(idString => UUID.fromString(idString))
        val clientReport: ClientReport = clientReportService.getReport(
          clientUuid = UUID.fromString(clientIdString),
          projectUuidList = projectUuids,
          employeeUuidList = employeeUuids,
          startDate = LocalDate.parse(startDateString),
          endDate = LocalDate.parse(endDateString),
          billable = billable,
          nonBillable = nonBillable
        )
        val clientReportJson = Json.toJson(clientReport)
        Ok(clientReportJson)

      } catch {
        case error: Exception =>
          logger.debug(s"""projectIdStringList = $projectIdStringList,
               |clientIdSting = $clientIdString,
               |startDateString = $startDateString,
               |endDateString = $endDateString,
               |employeeIdStringList = $employeeIdStringList
               |""".stripMargin)
          BadRequest(
            s"""{"message": "Error retrieving client report: $error"}"""
          ).as(JSON)
      }

    }

  def getSalaryReport(
    employeeIdString: String,
    clientIdStrings: List[String],
    startDateString: String,
    endDateString: String,
    billable: Boolean,
    nonBillable: Boolean
  ): Action[AnyContent] =
    Action {
      try {
        val clientUuids: List[UUID] =
          clientIdStrings.map(idString => UUID.fromString(idString))
        val salaryReport: SalaryReport = salaryReportService.getReport(
          employeeUuid = UUID.fromString(employeeIdString),
          clientUuidList = clientUuids,
          startDate = LocalDate.parse(startDateString),
          endDate = LocalDate.parse(endDateString),
          billable = billable,
          nonBillable = nonBillable
        )
        val salaryReportJson = Json.toJson(salaryReport)

        Ok(salaryReportJson)
      } catch {
        case error: Exception =>
          logger.debug(s"""employeeIdString = $employeeIdString,
               |clientIdStings = $clientIdStrings,
               |startDateString = $startDateString,
               |endDateString = $endDateString
               |""".stripMargin)
          BadRequest(
            s"""{"message": "Error retrieving client report: $error"}"""
          ).as(JSON)
      }

    }
}
