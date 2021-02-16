package controllers

import models.{
  ClientReport,
  ClientReportService,
  EmployeeSimple,
  ProjectSimple,
  TimeInputSimple
}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class ReportController @Inject() (
  cc: ControllerComponents,
  clientReportService: ClientReportService
) extends AbstractController(cc)
    with Logging {

  implicit val TimeInputSimpleWrites = Json.writes[TimeInputSimple]
  implicit val employeeSimpleWrites  = Json.writes[EmployeeSimple]
  implicit val projectSimpleWrites   = Json.writes[ProjectSimple]
  implicit val clientReportWrites    = Json.writes[ClientReport]

  def getClientReport(
    clientIdString: String,
    projectIdStringList: List[String],
    startDateString: String,
    endDateString: String,
    employeeIdStringList: List[String]
  ): Action[AnyContent] =
    Action {

      val projectUuids: List[UUID] =
        projectIdStringList.map(idString => UUID.fromString(idString))
      val employeeUuids: List[UUID] =
        employeeIdStringList.map(idString => UUID.fromString(idString))

      logger.debug(
        s"""projectIdStringList = $projectIdStringList, projectUuids = $projectUuids"""
      )

      val clientReport = clientReportService.getReport(
        clientUuid = UUID.fromString(clientIdString),
        projectUuidList = projectUuids,
        employeeUuidList = employeeUuids,
        startDate = LocalDate.parse(startDateString),
        endDate = LocalDate.parse(endDateString)
      )
      val clientReportJson = Json.toJson(clientReport)

      Ok(clientReportJson)
    }

}
