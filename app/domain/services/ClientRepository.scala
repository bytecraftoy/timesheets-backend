package domain.services

import com.google.inject.ImplementedBy
import domain.models.{Client, InvalidDataException}
import persistence.dao.ClientDAO
import play.api.Logging

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentClientRepository])
trait ClientRepository extends Repository[Client] with Logging {
  def byId(clientId: UUID): Option[Client]
  def all: Seq[Client]
  def add(client: Client): Unit
}

class DevelopmentClientRepository @Inject() (clientDao: ClientDAO)
    extends ClientRepository {

  def byId(clientId: UUID): Option[Client] = clientDao.getById(clientId)

  def all: Seq[Client] = clientDao.getAll

  def validate(client: Client): Unit = {
    if (client.name.isEmpty) {
      val msg = "Client name empty"
      throw new InvalidDataException(msg)
    }
    if (client.email.isEmpty) {
      val msg = "Client e-mail empty"
      throw new InvalidDataException(msg)
    }
  }

  def add(client: Client): Unit = {
    validate(client)
    clientDao.add(client)
  }
}
