package controllers

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import javax.inject._
import play.api.mvc._
import models.{AddTimeInputDTO, TimeInput}

class TimeInputController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def listTimeInput: Action[AnyContent] =
    Action {
      val json = Json.toJson(TimeInput.all)
      Ok(json)
    }

  def addTimeInput(): Action[JsValue] =
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
          println(errors)
          BadRequest
        }
      }
    }
}
