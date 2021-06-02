package domain.services

import com.google.inject.ImplementedBy
import domain.models.{Repository, TimeInput}
import persistence.dao.TimeInputDAO
import play.api.Logging
import web.dto.{AddTimeInputDTO, CompactTimeInputDTO, UpdateTimeInputDTO}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[DevelopmentTimeInputRepository])
trait TimeInputRepository extends Repository[TimeInput] with Logging {
  def update(timeInput: TimeInput): Int

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput]

  def compactTimeInputsByProjectEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): Seq[CompactTimeInputDTO]

  def byEmployeeInterval(
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput]

  def timeInputsByProjectEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    startDate: LocalDate,
    endDate: LocalDate
  ): Seq[TimeInput]

  def dtoAsTimeInput(dto: AddTimeInputDTO): TimeInput
  def dtoAsTimeInput(dto: UpdateTimeInputDTO): TimeInput
  def timeInputAsCompactDTO(timeInput: TimeInput): CompactTimeInputDTO
}

class DevelopmentTimeInputRepository @Inject() (
  timeInputDAO: TimeInputDAO,
  projectRepository: ProjectRepository,
  userRepository: UserRepository
)(implicit executionContext: ExecutionContext)
    extends TimeInputRepository
    with Logging {

  def all: Seq[TimeInput] = timeInputDAO.getAll()

  def byId(id: UUID): TimeInput = timeInputDAO.getById(id)

  def byProject(i: UUID): Seq[TimeInput] = timeInputDAO.byProject(i)

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput] =
    timeInputDAO.byTimeInterval(start, end)

  def add(timeInput: TimeInput): Unit = timeInputDAO.add(timeInput)

  def update(timeInput: TimeInput): Int = timeInputDAO.update(timeInput)

  def timeInputsByProjectEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    startDate: LocalDate,
    endDate: LocalDate
  ): Seq[TimeInput] = {
    timeInputDAO.byProjectAndEmployeeInterval(
      projectId = projectId,
      employeeId = employeeId,
      start = startDate,
      end = endDate
    )
  }

  def compactTimeInputsByProjectEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): Seq[CompactTimeInputDTO] = {
    timeInputDAO
      .byProjectAndEmployeeInterval(projectId, employeeId, start, end)
      .map(timeInputAsCompactDTO)
  }

  def byEmployeeInterval(
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput] =
    timeInputDAO.byEmployeeInterval(
      employeeId = employeeId,
      start = start,
      end = end
    )

  def dtoAsTimeInput(dto: AddTimeInputDTO): TimeInput = {
    TimeInput(
      input = dto.input,
      project = projectRepository.byId(dto.project),
      employee = userRepository.byId(dto.employee),
      date = dto.date,
      description = dto.description
    )
  }

  def dtoAsTimeInput(dto: UpdateTimeInputDTO): TimeInput = {
    val beforeUpdateModel: TimeInput = this.byId(dto.id)
    TimeInput(
      id = dto.id,
      input = dto.input,
      project = beforeUpdateModel.project,
      employee = beforeUpdateModel.employee,
      date = beforeUpdateModel.date,
      description = dto.description
    )
  }

  def timeInputAsCompactDTO(timeInput: TimeInput): CompactTimeInputDTO = {
    CompactTimeInputDTO(
      id = timeInput.id,
      input = timeInput.input,
      date = timeInput.date,
      created = timeInput.created,
      edited = timeInput.edited,
      description = timeInput.description
    )
  }
}
