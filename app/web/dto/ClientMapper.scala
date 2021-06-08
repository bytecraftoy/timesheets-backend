package web.dto

import com.google.inject.ImplementedBy
import domain.models.Client
import play.api.Logging

@ImplementedBy(classOf[DevelopmentClientMapper])
trait ClientMapper {
  def dtoAsClient(dto: AddClientDTO): Client
}

class DevelopmentClientMapper extends ClientMapper with Logging {

  def dtoAsClient(dto: AddClientDTO): Client =
    Client(name = dto.name, email = dto.email)

}
