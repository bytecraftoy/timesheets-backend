package controllers

import dto.AddTimeInputDTO

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

import java.util.UUID
import scala.concurrent.ExecutionContext

class TimeInputController @Inject() (
  cc: ControllerComponents,
  timeInputRepository: TimeInputRepository,
  projectRepository: ProjectRepository
)(implicit executionContext: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  case class AddTimeInputDTO(
    input: Long,
    project: UUID,
    employee: UUID,
    date: String
  ) {

    def asTimeInput: TimeInput = {
      TimeInput(
        input = this.input,
        project = projectRepository.byId(this.project),
        employee = User.byId(this.employee),
        date =
          LocalDate.parse(
            this.date
          ) // dateInput must be a String in format "yyyy-MM-dd"
      )
    }
  }

  object AddTimeInputDTO {
    implicit val readTimeInputDTO: Reads[AddTimeInputDTO] =
      Json.reads[AddTimeInputDTO]
  }
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
      val startDate = LocalDate.parse(start)
      val endDate   = LocalDate.parse(end)
      if (startDate.isAfter(endDate)) {
        // TODO: log error
      }
      val timeInput =
        timeInputRepository.byTimeInterval(
          startDate.minusDays(1),
          endDate.plusDays(1)
        )
      val json = Json.toJson(timeInput)
      Ok(json)
    }

  def add(): Action[JsValue] =
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
                if (
                  timeInputRepository.all.exists(
                    tir =>
                      tir.id == timeInput.id
                        || (tir.employee == timeInput.employee
                          && tir.project == timeInput.project
                          && tir.date == timeInput.date)
                  ) // TODO: consider moving this uniqueness check to database
                ) {
                  val msg = "Input must not be a duplicate of any existing"
                  logger.error(msg)
                  Conflict(Json.obj("message" -> msg))
                } else {
                  timeInputRepository.add(timeInput)
                  Ok(Json.toJson(timeInput))
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
      val json = timeInputRepository.jsonByProject(
        i = UUID.fromString(id),
        employeeId = UUID.fromString(employee),
        start = startDate,
        end = endDate
      )
      Ok(json)
    }

  def groupByProject(
    employee: String,
    start: String,
    end: String
  ): Action[AnyContent] =
    Action {
      val startDate =
        if (start == "getAll") LocalDate.MIN else LocalDate.parse(start)
      val endDate =
        if (start == "getAll") LocalDate.MAX else LocalDate.parse(end)
      val json = timeInputRepository.jsonGroupedByProject(
        employeeId = UUID.fromString(employee),
        start = startDate,
        end = endDate
      )
      Ok(json)
    }
}
