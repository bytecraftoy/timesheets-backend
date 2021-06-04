package domain.services

import com.google.inject.ImplementedBy
import domain.models.Client
import persistence.dao.ClientDAO
import play.api.Logging

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentClientRepository])
trait ClientRepository extends Repository[Client] with Logging {
  def byId(clientId: UUID): Client
  def all: Seq[Client]
  def add(client: Client): Unit
}

class DevelopmentClientRepository @Inject() (clientDao: ClientDAO)
    extends ClientRepository {

  def byId(clientId: UUID): Client = clientDao.getById(clientId)

  def all: Seq[Client] = clientDao.getAll()

  def add(client: Client): Unit = {
    if (client.name.isEmpty) {
      val msg = "Client name empty"
      logger.error(msg)
      throw new Exception(msg)
    }

    if (client.email.isEmpty) {
      val msg = "Client e-mail empty"
      logger.error(msg)
      throw new Exception(msg)
    }

    clientDao.add(client)
  }
}
