package controllers

import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json, OFormat, Reads}

import javax.inject._
import play.api.mvc._
import models.{Client, ClientRepository, Project, ProjectRepository, User}

import java.util.UUID

// https://www.playframework.com/documentation/2.8.x/ScalaJsonHttp

class ProjectController @Inject() (
  cc: ControllerComponents,
  projectRepo: ProjectRepository,
  clientRepo: ClientRepository
) extends AbstractController(cc) {

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

  case class AddProjectDTO(
    name: String,
    description: String,
    client: UUID,
    owner: UUID,
    billable: Boolean
  ) {
    def asProject: Project =
      Project(
        name = this.name,
        description = this.description,
        client = clientRepo.byId(this.client),
        owner = User.byId(this.owner),
        creator = User.byId(this.owner),
        managers = List(User.byId(this.owner)),
        lastEditor = User.byId(this.owner),
        billable = this.billable
      )
  }
  object AddProjectDTO {
    implicit val readProjectDTO: Reads[AddProjectDTO] =
      Json.reads[AddProjectDTO]
  }
}
