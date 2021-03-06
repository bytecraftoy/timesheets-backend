package domain.services

import com.google.inject.ImplementedBy
import domain.models._
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
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
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
            cost = HourlyCost(
              complexTimeInput.project.hourlyCost.value * complexTimeInput.input / 60,
              complexTimeInput.project.hourlyCost.currency
            ),
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

    employeesToInclude
      .sortBy(user => user.firstName)
      .sortBy(user => user.lastName)
      .map { employee =>
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
          val employeeTotalCost: HourlyCost = HourlyCost(
            value =
              project.hourlyCost.value * employeeTotal / 60,
            currency = project.hourlyCost.currency
          )

          EmployeeSimple(
            id = employee.id,
            firstName = employee.firstName,
            lastName = employee.lastName,
            employeeTotal = employeeTotal,
            employeeTotalCost = employeeTotalCost,
            timeInputs = simpleTimeInputs
          )
        }
      }
  }

  def getSimpleProjects(
    projectUuidList: List[UUID],
    employeeUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): List[ProjectSimple] = {

    logger.debug(
      s"""ClientReportService -> getSimpleProjects(), projectList = $projectUuidList, start = $startDate, end = $endDate"""
    )

    val complexProjects: List[Project] =
      projectUuidList.flatMap(uuid => projectRepo.byId(uuid))

    val simpleProjects: List[ProjectSimple] = complexProjects
      .filter(
        project =>
          if (billable == true && nonBillable == true) { // this case shows all
            project.billable == true || project.billable == false
          } else if (billable == true && nonBillable == false) {
            project.billable == true
          } else if (nonBillable == true && billable == false) {
            project.billable == false
          } else { // both false so show none
            false
          }
      )
      .sortBy(project => project.name)
      .map { project =>
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
          val projectTotalCost: HourlyCost = HourlyCost(
            value =
              project.hourlyCost.value * projectTotal / 60,
            currency = project.hourlyCost.currency
          )

          ProjectSimple(
            id = project.id,
            name = project.name,
            projectTotal = projectTotal,
            projectTotalCost = projectTotalCost,
            billable = project.billable,
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
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): ClientReport = {

    logger.debug(s"""ClientReportService -> getReport(),
         |client = $clientUuid,
         |projectList = $projectUuidList,
         |start = $startDate,
         |end = $endDate,
         |billable = $billable,
         |nonBillable = $nonBillable""".stripMargin)

    val client: Client = clientRepo.byId(clientUuid).get // TODO: avoid calling get
    val simpleProjects: List[ProjectSimple] = getSimpleProjects(
      projectUuidList = projectUuidList,
      employeeUuidList = employeeUuidList,
      startDate = startDate,
      endDate = endDate,
      billable = billable,
      nonBillable = nonBillable
    )

    val grandTotal = simpleProjects.foldRight(0L) { (project, i) =>
      project.projectTotal + i
    }
    val grandTotalCost: HourlyCost = HourlyCost(
      value = simpleProjects.foldRight(BigDecimal(0)) { (project, i) =>
        project.projectTotalCost.value + i
      },
      currency =
        if (simpleProjects.nonEmpty)
          simpleProjects.head.projectTotalCost.currency
        else
          "EUR"
    ) // TODO: don't sum different project costs with potentially different currencies

    ClientReport(
      startDate = startDate,
      endDate = endDate,
      created = System.currentTimeMillis(),
      client = client,
      projects = simpleProjects,
      grandTotal = grandTotal,
      grandTotalCost = grandTotalCost,
      billable = billable,
      nonBillable = nonBillable
    )
  }
}
