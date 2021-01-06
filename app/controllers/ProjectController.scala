package controllers

import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json, OFormat}

import javax.inject._
import play.api.mvc._
import models.{AddProjectDTO, Project, ProjectRepository}
import dao.ProjectDAO

// https://www.playframework.com/documentation/2.8.x/ScalaJsonHttp

class ProjectController @Inject() (cc: ControllerComponents,
                                   projectRepo: ProjectRepository)
    extends AbstractController(cc) {

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  def listProjects: Action[AnyContent] =
    Action {
      val json = Json.toJson(projectRepo.all)
      Ok(json)
    }


  val addProject: Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[AddProjectDTO] match {
      case JsSuccess(createProjectDTO, _) => {
        createProjectDTO.asProject match {
          case p: Project => {
            projectRepo.add(p)
            Ok(Json.toJson(p))
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
