package controllers

import io.swagger.annotations.Api

import java.time.LocalDate
import play.api.libs.json.{
  Format,
  JsError,
  JsSuccess,
  JsValue,
  Json,
  OFormat,
  Reads
}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import javax.inject._
import play.api.mvc._
import models.{
  Client,
  Project,
  ProjectRepository,
  TimeInput,
  TimeInputRepository,
  User
}
import play.api.Logging

import java.lang.IllegalArgumentException
import java.time.format.DateTimeParseException
import java.util.{Date, UUID}
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

@Api
class TimeInputController @Inject() (
  cc: ControllerComponents,
  timeInputRepository: TimeInputRepository,
  projectRepository: ProjectRepository
)(implicit executionContext: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  //TODO needs agreement with the team on simplifying the DTO as only one for add and update
  case class AddTimeInputDTO(
    input: Long,
    project: UUID,
    employee: UUID,
    date: String,
    description: String = ""
  ) {

    def asTimeInput: TimeInput = {
      TimeInput(
        input = this.input,
        project = projectRepository.byId(this.project),
        employee = User.byId(this.employee),
        date =
          LocalDate.parse(
            this.date
          ), // dateInput must be a String in format "yyyy-MM-dd"
        description = this.description
      )
    }
  }
  object AddTimeInputDTO {
    implicit val readTimeInputDTO: Reads[AddTimeInputDTO] =
      Json.using[Json.WithDefaultValues].reads[AddTimeInputDTO]
  }
  implicit def timeInputFormat: OFormat[TimeInput] =
    Json.using[Json.WithDefaultValues].format[TimeInput]

  case class UpdateTimeInputDTO(
    id: UUID,
    input: Long,
    description: String = ""
  ) {

    def asTimeInput: TimeInput = {
      val beforeUpdateModel: TimeInput = timeInputRepository.byId(this.id)

      TimeInput(
        id = this.id,
        input = this.input,
        project = beforeUpdateModel.project,
        employee = beforeUpdateModel.employee,
        date = beforeUpdateModel.date,
        description = this.description
      )
    }
  }
  object UpdateTimeInputDTO {
    implicit val readTimeInputDTO: Reads[UpdateTimeInputDTO] =
      Json.using[Json.WithDefaultValues].reads[UpdateTimeInputDTO]
  }

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
      val startDate = LocalDate.parse(start)
      val endDate   = LocalDate.parse(end)
      if (startDate.isAfter(endDate)) {
        val msg =
          s"""Start date is later than end date. start = $startDate, end = $endDate"""
        logger.error(msg)
        BadRequest(Json.obj("message" -> msg))
      } else {
        try {
          val timeInput = timeInputRepository.byTimeInterval(startDate, endDate)
          val json      = Json.toJson(timeInput)
          Ok(json)
        } catch {
          case error: Exception =>
            logger.error(error.getMessage)
            val msg =
              s"""Error retrieving timeinput byInterval: $error"""
            BadRequest(Json.obj("message" -> msg))
        }

      }
    }

  //TODO needs refactor, a validation pattern and more concise and better error handling
  def add(): Action[JsValue] = {
    logger.debug("TimeInputController.add()")

    Action(parse.json) { implicit request =>
      request.body.validate[AddTimeInputDTO] match {
        case JsSuccess(createTimeInputDTO, _) => {
          createTimeInputDTO.asTimeInput match {
            case timeInput: TimeInput => {
              if (timeInput.input < 0) {
                val msg = "Time must be non-negative"
                logger.error(msg)
                BadRequest(
                  Json.obj("message" -> msg)
                ) // TODO: consider moving this check to DTO
              } else {
                try {
                  timeInputRepository.add(timeInput)
                  Ok(Json.toJson(timeInput))
                } catch {
                  case error: Exception =>
                    logger.error(error.getMessage)
                    val msg =
                      s"""Error inserting a client: $error"""
                    BadRequest(Json.obj("message" -> msg))
                }
              }
            }
            case other => {
              logger.error(other.toString)
              InternalServerError(
                Json.obj("message" -> other.toString)
              ) // TODO: handle more specific cases
            }
          }
        }
        case JsError(errors) => {
          logger.error(errors.toString)
          BadRequest(
            Json.obj("message" -> errors.toString())
          ) // TODO: more specific error code
        }
      }
    }
  }

  def update(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validate[UpdateTimeInputDTO] match {
        case JsSuccess(updateTimeInputDTO, _) => {
          updateTimeInputDTO.asTimeInput match {
            case timeInput: TimeInput => {
              if (timeInput.input < 0) {
                val msg = "Time must be non-negative"
                logger.error(msg)
                BadRequest(
                  Json.obj("message" -> msg)
                ) // TODO: consider moving this check to DTO
              } else {
                try {
                  val updateCount: Int = timeInputRepository.update(timeInput)
                  if (updateCount > 0) {
                    val msg =
                      s"""Timeinput (${timeInput.id}) update successful."""
                    logger.debug(msg)
                    Ok(Json.toJson(timeInput))
                  } else {
                    val msg =
                      s"""Nothing updated. TimeInput ID = ${timeInput.id}"""
                    logger.error(msg)
                    BadRequest(Json.obj("message" -> msg))
                  }
                } catch {
                  case error: Exception =>
                    val msg =
                      s"""Error updating a client ($timeInput), error: $error, ${error
                        .printStackTrace()}"""
                    logger.error(msg)
                    BadRequest(Json.obj("message" -> msg))
                }
              }
            }
            case other => {
              logger.error(other.toString)
              InternalServerError(
                Json.obj("message" -> other.toString)
              ) // TODO: handle more specific cases
            }
          }
        }
        case JsError(errors) => {
          logger.error(errors.toString)
          BadRequest(
            Json.obj("message" -> errors.toString())
          ) // TODO: more specific error code
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
      val startDate =
        if (start == "getAll") LocalDate.MIN else LocalDate.parse(start)
      val endDate =
        if (start == "getAll") LocalDate.MAX else LocalDate.parse(end)
      try {
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
            s"""Error retrieving timeinput byProject: $error"""
          logger.error(msg)
          BadRequest(Json.obj("message" -> msg))
      }
    }

  def groupByProject(
    employeeId: String,
    start: String,
    end: String
  ): Action[AnyContent] =
    Action {
      val startDate =
        if (start == "getAll") LocalDate.MIN else LocalDate.parse(start)
      val endDate =
        if (start == "getAll") LocalDate.MAX else LocalDate.parse(end)
      try {
        val json = timeInputRepository.jsonGroupedByProject(
          employeeId = UUID.fromString(employeeId),
          start = startDate,
          end = endDate
        )
        Ok(json)
      } catch {
        case error: Exception =>
          val msg =
            s"""Error retrieving timeinput groupByProject, employeeId = $employeeId, start = $start, end = $end, error: $error"""
          logger.error(msg)
          BadRequest(Json.obj("message" -> msg))
      }
    }
}
