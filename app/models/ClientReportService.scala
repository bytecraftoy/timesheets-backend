package models
import com.google.inject.ImplementedBy
import play.api.Logging

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentClientReportService])
trait ClientReportService {

  def getReport(
    clientUuid: UUID,
    projectUuidList: List[UUID],
    employeeUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate
  ): ClientReport

}

class DevelopmentClientReportService @Inject() (
  clientRepo: ClientRepository,
  projectRepo: ProjectRepository,
  timeInputRepo: TimeInputRepository
) extends ClientReportService
    with Logging {

  def getSimpleTimeInputs(
    employee: User,
    project: Project,
    startDate: LocalDate,
    endDate: LocalDate
  ): List[TimeInputSimple] = {

    logger.debug(
      s"""ClientReportService -> getSimpleTimeInputs(), employee = $employee, project = $project, start = $startDate, end = $endDate"""
    )
    val complexTimeInputs: List[TimeInput] = timeInputRepo
      .timeInputsByProjectEmployeeInterval(
        project.id,
        employee.id,
        startDate,
        endDate
      )
      .toList

    val simpleTimeInputs: List[TimeInputSimple] = complexTimeInputs
      .map(
        complexTimeInput =>
          TimeInputSimple(
            id = complexTimeInput.id,
            date = complexTimeInput.date,
            input = complexTimeInput.input,
            description = complexTimeInput.description
          )
      )

    simpleTimeInputs
  }

  def getSimpleEmployees(
    project: Project,
    employeeUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate
  ): List[EmployeeSimple] = {

    logger.debug(
      s"""ClientReportService -> getSimpleEmployees(), project = ${project.id}, project.employees = ${project.employees}, start = $startDate, end = $endDate""".stripMargin
    )

    val employeesToInclude: List[User] =
      project.employees.filter(user => employeeUuidList.contains(user.id))
    logger.debug(
      s"""ClientReportService -> getSimpleEmployees(), employeesToInclude = $employeesToInclude""".stripMargin
    )

    employeesToInclude.map { employee =>
      {

        val simpleTimeInputs: List[TimeInputSimple] = getSimpleTimeInputs(
          employee = employee,
          project = project,
          startDate = startDate,
          endDate = endDate
        )

        val employeeTotal: Long = simpleTimeInputs.foldRight(0L) {
          (timeInput, i) => timeInput.input + i
        }

        EmployeeSimple(
          id = employee.id,
          firstName = employee.firstName,
          lastName = employee.lastName,
          employeeTotal = employeeTotal,
          timeInputs = simpleTimeInputs
        )
      }
    }
  }

  def getSimpleProjects(
    projectUuidList: List[UUID],
    employeeUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate
  ): List[ProjectSimple] = {

    logger.debug(
      s"""ClientReportService -> getSimpleProjects(), projectList = $projectUuidList, start = $startDate, end = $endDate"""
    )

    val complexProjects: List[Project] =
      projectUuidList.map(uuid => projectRepo.byId(uuid))

    val simpleProjects: List[ProjectSimple] = complexProjects.map { project =>
      {
        val simpleEmployees: List[EmployeeSimple] = getSimpleEmployees(
          project = project,
          employeeUuidList = employeeUuidList,
          startDate = startDate,
          endDate = endDate
        )

        val projectTotal: Long = simpleEmployees.foldRight(0L) {
          (employee, i) => employee.employeeTotal + i
        }

        ProjectSimple(
          id = project.id,
          name = project.name,
          projectTotal = projectTotal,
          employees = simpleEmployees
        )
      }
    }

    simpleProjects
  }

  def getReport(
    clientUuid: UUID,
    projectUuidList: List[UUID],
    employeeUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate
  ): ClientReport = {

    logger.debug(s"""ClientReportService -> getReport(),
         |client = $clientUuid,
         |projectList = $projectUuidList,
         |start = $startDate,
         |end = $endDate""".stripMargin)

    val client: Client = clientRepo.byId(clientUuid)
    val simpleProjects: List[ProjectSimple] = getSimpleProjects(
      projectUuidList = projectUuidList,
      employeeUuidList = employeeUuidList,
      startDate = startDate,
      endDate = endDate
    )

    val grandTotal = simpleProjects.foldRight(0L) { (project, i) =>
      project.projectTotal + i
    }

    ClientReport(
      startDate = startDate,
      endDate = endDate,
      creationMillis = System.currentTimeMillis(),
      client = client,
      projects = simpleProjects,
      grandTotal = grandTotal
    )
  }
}
