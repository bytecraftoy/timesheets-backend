package dto

import com.google.inject.ImplementedBy
import models.{ProjectRepository, TimeInput, User}

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[AddTimeInputDTO])
trait TimeInputDTO {
  def asTimeInput: TimeInput
}

class AddTimeInputDTO @Inject() (projectRepository: ProjectRepository)(
  input: Long,
  project: UUID,
  employee: UUID,
  date: String,
  description: String
) extends TimeInputDTO {
  def asTimeInput: TimeInput = {
    TimeInput(
      input = this.input,
      project = projectRepository.byId(this.project),
      employee = User.byId(this.employee),
      date =
        LocalDate.parse(
          this.date
        ), // dateInput must be a String in format "yyyy-MM-dd"
      description = this.description
    )
  }
  implicit def apply(
    input: Long,
    project: String,
    employee: String,
    date: String,
    description: String
  ): AddTimeInputDTO =
    new AddTimeInputDTO(projectRepository)(
      input,
      UUID.fromString(project),
      UUID.fromString(employee),
      date,
      description
    )

}
