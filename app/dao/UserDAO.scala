package dao

import anorm.ResultSetParser
import models.User
import anorm._
import com.google.inject.ImplementedBy
import play.api.Logging
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[UserDAOAnorm])
trait UserDAO extends DAO[User] {
  def getAll(): Seq[User]
  def getById(userId: UUID): User
  def add(user: User): Unit
  def getAllManagers(): Seq[User]
  def getManagersByProjectId(projectId: UUID): Seq[User]
}

// https://gist.github.com/davegurnell/4b432066b39949850b04
class UserDAOAnorm @Inject() (db: Database) extends UserDAO with Logging {
  def getAll(): Seq[User] = {
    val sql = "SELECT * FROM app_user;"
    logger.debug(s"UserDAOAnorm.getAll(), SQL = $sql")
    getUsers(sql)
  }

  def getAllManagers(): Seq[User] = {
    val sql = "SELECT * FROM app_user where is_manager = true;"
    logger.debug(s"UserDAOAnorm.getAllManagers(), SQL = $sql")
    getUsers(sql)
  }

  def getManagersByProjectId(projectId: UUID): Seq[User] = {
    val sql =
      s"SELECT * FROM app_user where is_manager = true and app_user_id IN " +
        s"(SELECT app_user_id FROM project_app_user where project_id = '$projectId');"
    logger.debug(s"UserDAOAnorm.getManagersByProjectId, SQL = $sql")
    getUsers(sql)

  }

  def getById(userId: UUID): User = {
    val sql = "SELECT * FROM app_user WHERE app_user_id = '" + userId + "';"
    logger.debug(s"UserDAOAnorm.getById(), SQL = $sql")
    val results = getUsers(sql)
    if (results.isEmpty) {
      null
    } else {
      results.head
    }
  }

  def add(user: User): Unit = {
    db.withConnection { implicit connection =>
      val sql =
        "INSERT INTO app_user (app_user_id, username, first_name, last_name, email, phone_number, salary, is_manager, timestamp_created ~ timestamp_edited)" +
          " VALUES ({id}::uuid, {username}, {firstName}, {lastName}, {email}, {phoneNumber}, {salary}, {isManager}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);"
      logger.debug(s"UserDAOAnorm.add, SQL = $sql")
      SQL(sql)
        .bind(user)
        .executeInsert(anorm.SqlParser.scalar[java.util.UUID].singleOpt)
    }
  }

  def getUsers(sql: String): Seq[User] =
    db.withConnection { implicit c =>
      val userParser: RowParser[User] = (
        SqlParser.get[UUID]("app_user_id") ~
          SqlParser.str("username") ~
          SqlParser.str("first_name") ~
          SqlParser.str("last_name") ~
          SqlParser.get[Option[String]]("email") ~
          SqlParser.get[Option[String]]("phone_number") ~
          SqlParser.get[Option[BigDecimal]]("salary") ~
          SqlParser.bool("is_manager") ~
          SqlParser.date("timestamp_created") ~
          SqlParser.date("timestamp_edited")
      ) map {
        case app_user_id ~ username ~ first_name ~ last_name ~ email ~ phone_number ~ salary ~ is_manager ~ timestamp_created ~ timestamp_edited =>
          User(
            id = app_user_id,
            username = username,
            firstName = first_name,
            lastName = last_name,
            email = email getOrElse "",
            phoneNumber = phone_number getOrElse "",
            salary = salary getOrElse BigDecimal(0),
            isManager = is_manager,
            creationTimestamp = timestamp_created.getTime(),
            lastEdited = timestamp_edited.getTime()
          )
      }
      val allUsersParser: ResultSetParser[List[User]] = userParser.*
      val userResult: List[User]                      = SQL(sql).as(allUsersParser)
      userResult
    }
}