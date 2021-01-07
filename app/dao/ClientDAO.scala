package dao

import anorm.ResultSetParser
import models.Client
import anorm._
import com.google.inject.ImplementedBy
import play.api.Logging
import play.api.db.Database

import java.sql.Timestamp
import java.time.Clock
import java.util.{Calendar, UUID}
import javax.inject.Inject

@ImplementedBy(classOf[ClientDAOAnorm])
trait ClientDAO extends DAO[Client] {
  def getAll(): Seq[Client]
  def getById(clientId: UUID): Client
  def add(client: Client): Unit
}

// https://gist.github.com/davegurnell/4b432066b39949850b04
class ClientDAOAnorm @Inject() (db: Database) extends ClientDAO with Logging {
  def getAll(): Seq[Client] = {
    val sql = "SELECT * FROM client;"
    logger.debug(s"ClientDAOAnorm.getAll(), SQL = $sql")
    getClients(sql)
  }

  def getById(clientId: UUID): Client = {
    val sql = "SELECT * FROM client WHERE client_id = '" + clientId + "';"
    logger.debug(s"ClientDAOAnorm.getById(), SQL = $sql")
    val results = getClients(sql)
    if (results.isEmpty) {
      null
    } else {
      results.head
    }
  }

  def add(client: Client): Unit = {
    db.withConnection { implicit connection =>
      val sql =
        "INSERT INTO client (client_id, name, email, timestamp_created, timestamp_edited)" +
          " VALUES ({id}, {name}, {email}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);"
      logger.debug("ClientDAOAnorm.add, SQL = $sql")
      SQL(sql).bind(client).executeInsert()
    }
  }

  def getClients(sql: String): Seq[Client] =
    db.withConnection { implicit c =>
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
            timestamp_created = timestamp_created.getTime(),
            timestamp_edited = timestamp_edited.getTime()
          )
      }
      val allClientsParser: ResultSetParser[List[Client]] = clientParser.*
      val clientResult: List[Client]                      = SQL(sql).as(allClientsParser)
      clientResult
    }
}
