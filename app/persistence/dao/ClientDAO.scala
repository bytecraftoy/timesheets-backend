package persistence.dao

import anorm.{ResultSetParser, _}
import com.google.inject.ImplementedBy
import domain.models.Client
import play.api.Logging
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[ClientDAOAnorm])
trait ClientDAO extends DAO[Client] {
  def getAll(): Seq[Client]
  def getById(clientId: UUID): Client
  def add(client: Client): Unit
}

// https://gist.github.com/davegurnell/4b432066b39949850b04
class ClientDAOAnorm @Inject() (db: Database) extends ClientDAO with Logging {

  val clientParser: RowParser[Client] = (
    SqlParser.get[UUID]("client_id") ~
      SqlParser.str("name") ~
      SqlParser.str("email") ~
      SqlParser.date("timestamp_created") ~
      SqlParser.date("timestamp_edited")
  ) map {
    case client_id ~ name ~ email ~ timestamp_created ~ timestamp_edited =>
      Client(
        id = client_id,
        name = name,
        email = email,
        created = timestamp_created.getTime(),
        edited = timestamp_edited.getTime()
      )
  }
  val allClientsParser: ResultSetParser[List[Client]] = clientParser.*

  def getAll(): Seq[Client] =
    db.withConnection { implicit c =>
      val sql = "SELECT * FROM client;"
      logger.debug(s"ClientDAOAnorm.getAll(), SQL = $sql")
      val clientResult: List[Client] = SQL(sql).as(allClientsParser)
      clientResult
    }

  def getById(clientId: UUID): Client =
    db.withConnection { implicit c =>
      val sql = "SELECT * FROM client WHERE client_id = {clientId}::uuid;"
      logger.debug(s"ClientDAOAnorm.getById(), SQL = $sql")
      val clientResults =
        SQL(sql).on("clientId" -> clientId).as(allClientsParser)
      if (clientResults.isEmpty) {
        null
      } else {
        clientResults.head
      }
    }

  def add(client: Client): Unit = {
    db.withConnection { implicit connection =>
      val sql =
        "INSERT INTO client (client_id, name, email, timestamp_created, timestamp_edited)" +
          " VALUES ({id}::uuid, {name}, {email}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);"
      logger.debug(s"ClientDAOAnorm.add, SQL = $sql")
      SQL(sql)
        .bind(client)
        .executeInsert(anorm.SqlParser.scalar[java.util.UUID].singleOpt)
    }
  }

}
