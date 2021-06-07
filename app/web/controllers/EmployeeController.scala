package web.controllers

import domain.models.{Client, Project, User}
import domain.services.{ClientRepository, ProjectRepository, UserRepository}
import io.swagger.annotations._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}

import java.util.UUID
import javax.inject.Inject

@Api
class EmployeeController @Inject() (
  cc: ControllerComponents,
  userRepository: UserRepository,
  clientRepository: ClientRepository,
  projectRepository: ProjectRepository
) extends AbstractController(cc)
    with Logging {

  @ApiOperation(value = "Get all employees, including managers")
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
  def listEmployees: Action[AnyContent] =
    Action {
      try {
        val employees     = userRepository.all
        val employeesJson = Json.toJson(employees)
        Ok(employeesJson)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error retrieving managers: $error"}""")
            .as(JSON)
      }
    }

  @ApiOperation(
    value =
      "Get all clients that have projects worked on by the specified employee"
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "OK",
        response = classOf[Client],
        responseContainer = "List"
      )
    )
  )
  def listClientsOfEmployee(
    @ApiParam(
      value = "UUID of the employee",
      required = true
    ) employeeId: String
  ): Action[AnyContent] =
    Action {
      try {
        val employeeUuid: UUID        = UUID.fromString(employeeId)
        val allClients: Seq[Client]   = clientRepository.all
        val allProjects: Seq[Project] = projectRepository.all
        val clientsOfEmployee: Seq[Client] = allClients.filter { client =>
          val projectsContainingBoth = allProjects.filter(project => {
            project.client.id == client.id &&
              (project.employees.map(_.id).contains(employeeUuid) ||
                project.owner.id.equals(employeeUuid))
          })
          projectsContainingBoth.nonEmpty
        }
        val clientsOfEmployeeJson = Json.toJson(clientsOfEmployee)
        Ok(clientsOfEmployeeJson)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error retrieving managers: $error"}""")
            .as(JSON)
      }

    }

}
