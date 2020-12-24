package controllers

import java.time.LocalDate

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import javax.inject._
import play.api.mvc._
import models.{AddTimeInputDTO, TimeInput}

class TimeInputController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def getData(start: String, end: String): Action[AnyContent] = {
    if (start == "getAll") {
      getAll()
    } else {
      byInterval(start, end)
    }
  }

  def getAll: Action[AnyContent] =
    Action {
      val json = Json.toJson(TimeInput.all)
      Ok(json)
    }

  def byProject(id: Long): Action[AnyContent] =
    Action {
      val json = Json.toJson(TimeInput.byProject(id))
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
        TimeInput.byTimeInterval(startDate.minusDays(1), endDate.plusDays(1))
      val json = Json.toJson(timeInput)
      Ok(json)
    }

  def add(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validate[AddTimeInputDTO] match {
        case JsSuccess(createTimeInputDTO, _) => {
          createTimeInputDTO.asTimeInput match {
            case t: TimeInput => {
              TimeInput.add(t)
              Ok(Json.toJson(t))
            }
            case other => InternalServerError
          }
        }
        case JsError(errors) => {
          BadRequest // TODO: log errors
        }
      }
    }
}
