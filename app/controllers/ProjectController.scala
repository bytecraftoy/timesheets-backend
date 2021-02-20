package controllers

import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import models._
import play.api.Logging
import play.api.libs.json._
import play.api.mvc._

import java.util.UUID
import javax.inject._

// https://www.playframework.com/documentation/2.8.x/ScalaJsonHttp

@Api
class ProjectController @Inject() (
  cc: ControllerComponents,
  projectRepo: ProjectRepository,
  clientRepo: ClientRepository,
  userRepo: UserRepository
) extends AbstractController(cc)
    with Logging {

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  @ApiOperation(value = "Get all projects")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "OK",
        response = classOf[Project],
        responseContainer = "List"
      )
    )
  )
  def listProjects: Action[AnyContent] =
    Action {
      val projectsAsJson = Json.toJson(projectRepo.all)
      Ok(projectsAsJson)
    }

  def listProjectsByClientId(clientId: String): Action[AnyContent] =
    Action {
      try {
        val clientUuid = UUID.fromString(clientId)
        val client     = clientRepo.byId(clientUuid)
        if (client != null) {
          val clientProjects = projectRepo.all.filter(_.client == client)
          val projectsAsJson = Json.toJson(clientProjects)
          Ok(projectsAsJson)
        } else {
          BadRequest(
            s"""{"message": "Error retrieving projects with client id = $clientId"}"""
          ).as(JSON)
        }

      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(
            s"""{"message": "Error retrieving a client's projects: $error"}"""
          ).as(JSON)
      }
    }

  def getUsersByProject(projectIds: List[String]): Action[AnyContent] =
    Action {
      try {
        val projectUuids: List[UUID] =
          projectIds.map(idString => UUID.fromString(idString))
        logger.debug(
          s"""projectIds = $projectIds, projectUuids = $projectUuids"""
        )

        val users =
          projectUuids.map(uuid => userRepo.getEmployeesByProjectId(uuid))
        val usersJson = Json.toJson(
          users.flatten.distinct
            .sortBy(user => user.lastName)
            .sortBy(user => user.firstName)
        )
        logger.debug(s"""usersJson = $usersJson""")
        Ok(usersJson)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(
            s"""{"message": "Error retrieving a client's projects: $error"}"""
          ).as(JSON)
      }
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
