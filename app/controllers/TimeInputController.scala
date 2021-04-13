package controllers

import dto.{AddTimeInputDTO, UpdateTimeInputDTO}
import io.swagger.annotations.Api
import models._
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json, OFormat, Reads, _}
import play.api.mvc._

import java.time.LocalDate
import java.util.UUID
import javax.inject._
import scala.concurrent.ExecutionContext

@Api
class TimeInputController @Inject() (
  cc: ControllerComponents,
  timeInputRepository: TimeInputRepository,
  projectRepository: ProjectRepository,
  userRepository: UserRepository
)(implicit executionContext: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  implicit def timeInputFormat: OFormat[TimeInput] =
    Json.using[Json.WithDefaultValues].format[TimeInput]

  def getData(start: String, end: String): Action[AnyContent] = {
    if (start == "getAll") {
      getAll()
    } else {
      byInterval(start, end)
    }
  }

  def getAll: Action[AnyContent] =
    Action {
      val json = Json.toJson(timeInputRepository.all)
      Ok(json)
    }

  def byInterval(start: String, end: String): Action[AnyContent] =
    Action {
      try {
        val startDate = LocalDate.parse(start)
        val endDate   = LocalDate.parse(end)
        if (startDate.isAfter(endDate)) {
          val msg =
            s"""Start date is later than end date. start = $startDate, end = $endDate"""
          logger.error(msg)
          BadRequest(Json.obj("message" -> msg))
        } else {
          val timeInput = timeInputRepository.byTimeInterval(startDate, endDate)
          val json      = Json.toJson(timeInput)
          Ok(json)
        }
      } catch {
        case error: Exception =>
          val msg =
            s"""Error retrieving timeinput. Error: ${error.getMessage}"""
          logger.error(
            msg + s""" At byInterval, start = $start, end = $end""",
            error
          )
          BadRequest(Json.obj("message" -> msg))
      }
    }

  def add(): Action[JsValue] = {
    Action(parse.json) { implicit request =>
      request.body.validate[AddTimeInputDTO] match {
        case JsSuccess(addTimeInputDTO, _) => {
          try {
            val timeInput = timeInputRepository.dtoAsTimeInput(addTimeInputDTO)
            timeInputRepository.add(timeInput)
            Ok(Json.toJson(timeInput))
          } catch {
            case error: Exception =>
              val msg =
                s"""Error adding a timeinput, error: ${error.getMessage}"""
              logger.error(msg, error)
              BadRequest(Json.obj("message" -> msg))
          }
        }
        case errors: JsError => {
          val msg = s"""Error adding a timeinput, error: ${errors.toString}"""
          logger.error(msg + s""", request body = ${request.body}""")
          BadRequest(Json.obj("message" -> msg))
        }
      }
    }
  }

  def update(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validate[UpdateTimeInputDTO] match {
        case JsSuccess(updateTimeInputDTO, _) => {
          try {
            val timeInput =
              timeInputRepository.dtoAsTimeInput(updateTimeInputDTO)
            val updateCount: Int = timeInputRepository.update(timeInput)
            if (updateCount > 0) {
              val msg = s"""Timeinput update successful."""
              logger.debug(msg + s""" Timeinput ID = ${timeInput.id}""")
              Ok(Json.toJson(timeInput))
            } else {
              val msg = s"""Nothing updated."""
              logger.error(msg + s"""" TimeInput ID = ${timeInput.id}""")
              BadRequest(Json.obj("message" -> msg))
            }
          } catch {
            case error: Exception =>
              val msg =
                s"""Error updating a timeinput, error: ${error.getMessage}"""
              logger.error(msg, error)
              BadRequest(Json.obj("message" -> msg))
          }
        }
        case errors: JsError => {
          val msg =
            s"""Error updating a timeinput, validation errors: ${errors.toString}"""
          logger.error(msg + s""", request body = ${request.body}""")
          BadRequest(Json.obj("message" -> msg))
        }
      }
    }

  def byProject(
    id: String,
    employee: String,
    start: String,
    end: String
  ): Action[AnyContent] =
    Action {
      try {
        val startDate =
          if (start == "getAll") LocalDate.MIN else LocalDate.parse(start)
        val endDate =
          if (start == "getAll") LocalDate.MAX else LocalDate.parse(end)
        val json = timeInputRepository.jsonByProject(
          projectId = UUID.fromString(id),
          employeeId = UUID.fromString(employee),
          start = startDate,
          end = endDate
        )
        Ok(json)
      } catch {
        case error: Exception =>
          val msg =
            s"""Error retrieving timeinput. Error: ${error.getMessage}"""
          logger.error(
            msg + s""" At byProject, id = $id, employee = $employee, start = $start, end = $end""",
            error
          )
          BadRequest(Json.obj("message" -> msg))
      }
    }

  def groupByProject(
    employeeId: String,
    start: String,
    end: String
  ): Action[AnyContent] =
    Action {
      try {
        val startDate =
          if (start == "getAll") LocalDate.MIN else LocalDate.parse(start)
        val endDate =
          if (start == "getAll") LocalDate.MAX else LocalDate.parse(end)
        val json = timeInputRepository.jsonGroupedByProject(
          employeeId = UUID.fromString(employeeId),
          start = startDate,
          end = endDate
        )
        Ok(json)
      } catch {
        case error: Exception =>
          val msg =
            s"""Error retrieving timeinput. Error: ${error.getMessage}"""
          logger.error(
            msg + s""" At groupByProject, employeeId = $employeeId, start = $start, end = $end""",
            error
          )
          BadRequest(Json.obj("message" -> msg))
      }
    }
}
