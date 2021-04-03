package models

import com.google.inject.ImplementedBy
import play.api.Logging

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentSalaryReportService])
trait SalaryReportService {

  def getReport(
    employeeUuid: UUID,
    clientUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): SalaryReport

}

class DevelopmentSalaryReportService @Inject() (
  userRepo: UserRepository,
  clientRepo: ClientRepository,
  projectRepo: ProjectRepository,
  timeInputRepo: TimeInputRepository
) extends SalaryReportService
    with Logging {

  def getSimpleTimeInputs(
    projectId: UUID,
    employeeId: UUID,
    startDate: LocalDate,
    endDate: LocalDate
  ): Seq[SimpleTimeInput] = {
    val complexTimeInputs = timeInputRepo.timeInputsByProjectEmployeeInterval(
      projectId = projectId,
      employeeId = employeeId,
      startDate = startDate,
      endDate = endDate
    )
    complexTimeInputs.map { input =>
      SimpleTimeInput(
        id = input.id,
        description = input.description,
        input = input.input,
        created = input.creationTimestamp,
        updated = input.lastEdited,
        date = input.date
      )
    }
  }

  def getSimpleProjects(
    client: Client,
    employee: User,
    startDate: LocalDate,
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): List[SimpleProject] = {

    val allComplexProjects: List[Project] = projectRepo.all.toList

    val simpleProjects: List[SimpleProject] =
      allComplexProjects
        .filter(_.client == client)
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
            val timeInputs = getSimpleTimeInputs(
              projectId = project.id,
              employeeId = employee.id,
              startDate = startDate,
              endDate = endDate
            )
            val projectTotal: Long = timeInputs.foldLeft(0L) {
              (accumulator, input) => accumulator + input.input
            }
            SimpleProject(
              id = project.id,
              name = project.name,
              projectTotal = projectTotal,
              billable = project.billable,
              timeInputs = timeInputs.toList
            )
          }
        }
    simpleProjects
  }

  def getSimpleClients(
    clientUuidList: List[UUID],
    employee: User,
    startDate: LocalDate,
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): List[SimpleClient] = {

    val complexClients: List[Client] =
      clientUuidList.map(uuid => clientRepo.byId(uuid))

    val simpleClients: List[SimpleClient] = complexClients
      .sortBy(client => client.name)
      .map { client =>
        {
          val simpleProjects: List[SimpleProject] = getSimpleProjects(
            client = client,
            employee = employee,
            startDate = startDate,
            endDate = endDate,
            billable = billable,
            nonBillable = nonBillable
          )
          val clientTotal: Long = simpleProjects.foldLeft(0L) {
            (accumulator, project) => accumulator + project.projectTotal
          }
          SimpleClient(
            id = client.id,
            name = client.name,
            clientTotal = clientTotal,
            projects = simpleProjects
          )
        }
      }
    simpleClients
  }

  def getReport(
    employeeUuid: UUID,
    clientUuidList: List[UUID],
    startDate: LocalDate,
    endDate: LocalDate,
    billable: Boolean,
    nonBillable: Boolean
  ): SalaryReport = {

    logger.debug(s"""
         |SalaryReportService -> getReport(),
         |employeeUuid = $employeeUuid,
         |clientUuidList = $clientUuidList,
         |start = $startDate,
         |end = $endDate,
         |billable = $billable,
         |nonBillable = $nonBillable""".stripMargin)

    val employee: User = userRepo.byId(employeeUuid)
    val simpleClients: List[SimpleClient] = getSimpleClients(
      clientUuidList = clientUuidList,
      employee = employee,
      startDate = startDate,
      endDate = endDate,
      billable: Boolean,
      nonBillable: Boolean
    )

    val grandTotal: Long = simpleClients.foldLeft(0L) { (accumulator, client) =>
      accumulator + client.clientTotal
    }

    SalaryReport(
      startDate = startDate,
      endDate = endDate,
      employee = employee,
      grandTotal = grandTotal,
      billable = billable,
      nonBillable = nonBillable,
      clients = simpleClients
    )

  }
}
