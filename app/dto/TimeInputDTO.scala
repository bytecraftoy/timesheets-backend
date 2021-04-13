package dto

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Reads.min
import play.api.libs.json.{JsError, JsPath, Reads}

import java.time.LocalDate
import java.util.UUID

case class TimeInputDTO()

case class AddTimeInputDTO(
  input: Long,
  project: UUID,
  employee: UUID,
  date: LocalDate,
  description: String = ""
) {}
object AddTimeInputDTO {
  implicit val addTimeInputDTOReads: Reads[AddTimeInputDTO] = (
    (JsPath \ "input")
      .read[Long](min[Long](0))
      .orElse(
        Reads(_ => JsError("""Time input has to be a positive integer."""))
      ) and
      (JsPath \ "project").read[UUID] and
      (JsPath \ "employee").read[UUID] and
      (JsPath \ "date").read[LocalDate] and
      (JsPath \ "description").read[String]
  )(AddTimeInputDTO.apply _)
}
case class UpdateTimeInputDTO(
  id: UUID,
  input: Long,
  description: String = ""
) {}
object UpdateTimeInputDTO {
  implicit val updateTimeInputDTOReads: Reads[UpdateTimeInputDTO] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "input")
        .read[Long](min[Long](0))
        .orElse(
          Reads(_ => JsError("""Time input has to be a positive integer."""))
        ) and
      (JsPath \ "description").read[String]
  )(UpdateTimeInputDTO.apply _)
}
