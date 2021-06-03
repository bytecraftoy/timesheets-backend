package domain.services

import com.google.inject.ImplementedBy
import domain.models._
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
        cost = HourlyCost(
          input.project.hourlyCost.value * input.input / 60,
          input.project.hourlyCost.currency
        ),
        created = input.created,
        updated = input.edited,
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
            val projectTotalCost = HourlyCost(
              project.hourlyCost.value * projectTotal / 60,
              project.hourlyCost.currency
            )
            SimpleProject(
              id = project.id,
              name = project.name,
              projectTotal = projectTotal,
              projectTotalCost = projectTotalCost,
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
      clientUuidList.map(uuid => clientRepo.byId(uuid).get) // TODO: avoid calling get

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
          val clientTotalCurrency: String = {
            if (simpleProjects.nonEmpty)
              simpleProjects.head.projectTotalCost.currency
            else "EUR"
          }
          val clientTotalCost: HourlyCost =
            simpleProjects.foldLeft(HourlyCost(0, clientTotalCurrency)) {
              (accumulator, project) =>
                accumulator + project.projectTotalCost
            } // TODO: don't sum different project costs with potentially different currencies
          SimpleClient(
            id = client.id,
            name = client.name,
            clientTotal = clientTotal,
            clientTotalCost = clientTotalCost,
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

    val employee: User = userRepo.byId(employeeUuid).get // TODO: avoid calling get
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
    val grandTotalCurrency: String = {
      if (simpleClients.nonEmpty) simpleClients.head.clientTotalCost.currency
      else "EUR"
    }
    val grandTotalCost: HourlyCost =
      simpleClients.foldLeft(HourlyCost(0, grandTotalCurrency)) {
        (accumulator, client) =>
          accumulator + client.clientTotalCost
      }

    SalaryReport(
      startDate = startDate,
      endDate = endDate,
      employee = employee,
      grandTotal = grandTotal,
      grandTotalCost = grandTotalCost,
      billable = billable,
      nonBillable = nonBillable,
      clients = simpleClients
    )

  }
}
