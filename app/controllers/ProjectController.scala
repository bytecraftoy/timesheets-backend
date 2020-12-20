package controllers

import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json}

import javax.inject._
import play.api.mvc._
import models.{AddProjectDTO, Project}

// https://www.playframework.com/documentation/2.8.x/ScalaJsonHttp

class ProjectController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def listProjects: Action[AnyContent] =
    Action {
      val json = Json.toJson(Project.all)
      Ok(json)
    }

  val addProject: Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[AddProjectDTO] match {
      case JsSuccess(createProjectDTO, _) => {
        createProjectDTO.asProject match {
          case p: Project => {
            Project.add(p)
            Ok(Json.toJson(p))
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
