package models

import com.google.inject.ImplementedBy
import dao.TimeInputDAO
import play.api.Logging
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@ImplementedBy(classOf[DevelopmentTimeInputRepository])
trait TimeInputRepository extends Repository[TimeInput] with Logging {
  def update(timeInput: TimeInput): Int

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput]

  def jsonByProject(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsValue

  def jsonGroupedByProject(
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsObject

  def timeInputsByProjectEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    startDate: LocalDate,
    endDate: LocalDate
  ): Seq[TimeInput]
}

class DevelopmentTimeInputRepository @Inject() (
  timeInputDAO: TimeInputDAO,
  projectRepository: ProjectRepository
)(implicit executionContext: ExecutionContext)
    extends TimeInputRepository
    with Logging {

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  implicit def timeInputFormat: OFormat[TimeInput] = {
    Json.using[Json.WithDefaultValues].format[TimeInput]
  }

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

  def jsonByProject(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsValue = {

    Json.toJson(
      timeInputDAO
        .byProjectAndEmployeeInterval(projectId, employeeId, start, end)
        .map(
          ti =>
            Json.obj(
              "id"                -> ti.id,
              "input"             -> ti.input,
              "date"              -> ti.date,
              "creationTimestamp" -> ti.creationTimestamp,
              "lastEdited"        -> ti.lastEdited,
              "description"       -> ti.description
            )
        )
    )
  }

  def jsonGroupedByProject(
    employeeId: UUID,
    start: LocalDate = LocalDate.MIN,
    end: LocalDate = LocalDate.MAX
  ): JsObject = {
    val timeInputs: Seq[TimeInput] =
      timeInputDAO.byEmployeeInterval(employeeId, start, end)

    if (timeInputs.size > 0) {
      Json.obj(
        "projects" -> timeInputs
          .map(
            timeInput =>
              Json.obj(
                "id"          -> timeInput.id,
                "name"        -> projectRepository.byId(timeInput.project.id).name,
                "hours"       -> timeInput.input,
                "description" -> timeInput.description
              )
          )
      )
    } else {
      Json.obj("projects" -> "")
    }
  }
}
