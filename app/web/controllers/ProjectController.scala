package web.controllers

import domain.models.{Project, User}
import domain.services.{ClientRepository, ProjectRepository, UserRepository}
import io.swagger.annotations._
import play.api.Logging
import play.api.libs.json._
import play.api.mvc._
import web.dto.{AddProjectDTO, UpdateProjectDTO}

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

  @ApiOperation(value = "Get all projects of a client")
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
  def listProjectsByClientId(
    @ApiParam(value = "UUID of the client") clientId: String
  ): Action[AnyContent] =
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

  @ApiOperation(value = "Get all projects the employee works on")
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
  def listProjectsByEmployeeId(
    @ApiParam(value = "UUID of the employee") employeeId: String
  ): Action[AnyContent] =
    Action {
      val employeeUuid = UUID.fromString(employeeId)
      val projectsWithEmployee = projectRepo.all.filter(
        project =>
          project.employees.exists(_.id == employeeUuid)
            || project.managers.exists(_.id == employeeUuid)
            || project.owner.id == employeeUuid
      ) // TODO: optimize/implement at DAO level
      val projectsAsJson = Json.toJson(projectsWithEmployee)
      Ok(projectsAsJson)
    }

  @ApiOperation(value = "Get all employees working on specified projects")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "OK",
        response = classOf[User],
        responseContainer = "List"
      )
    )
  )
  def getUsersByProject(
    @ApiParam(value = "List of project UUIDs") projectIds: List[String]
  ): Action[AnyContent] =
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

  @ApiOperation(value = "Insert new project")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "OK", response = classOf[Project])
    )
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "Project to add",
        paramType = "body",
        dataType = "web.dto.AddProjectDTO"
      )
    )
  )
  def addProject: Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validate[AddProjectDTO] match {
        case JsSuccess(createProjectDTO, _) => {
          projectRepo.dtoAsProject(createProjectDTO) match {
            case project: Project => {
              if (project.employees.exists(_.id == project.owner.id)) {
                val msg = "Owner cannot be an employee"
                logger.error(msg)
                BadRequest(Json.obj("message" -> msg))
              } else {
                projectRepo.add(project)
                Ok(Json.toJson(project))
              }
            }
            case other => InternalServerError
          }
        }
        case JsError(errors) => {
          logger.error(errors.toString())
          BadRequest
        }
      }
    }

  @ApiOperation(value = "Update existing project")
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "OK", response = classOf[Project])
    )
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "New project properties",
        paramType = "body",
        dataType = "web.dto.UpdateProjectDTO"
      )
    )
  )
  def updateProject: Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validate[UpdateProjectDTO] match {
        case JsSuccess(updateProjectDTO, _) => {
          projectRepo.dtoAsProject(updateProjectDTO) match {
            case project: Project => {
              if (project.employees.exists(_.id == project.owner.id)) {
                val msg = "Owner cannot be an employee"
                logger.error(msg)
                BadRequest(Json.obj("message" -> msg))
              } else {
                projectRepo.update(project)
                Ok(Json.toJson(project))
              }
            }
            case other => InternalServerError
          }
        }
        case JsError(errors) => {
          logger.error(errors.toString())
          BadRequest
        }
      }
    }

}
