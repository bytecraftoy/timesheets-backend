package dao

import anorm.{ResultSetParser, RowParser, SQL, SqlParser}
import models.{Client, Project, User}
import play.api.db.Database
import play.api.db.evolutions.Evolutions
import dao.DAO
import anorm._
import com.google.inject.ImplementedBy
import play.api.Logging

import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[ProjectDAOAnorm])
trait ProjectDAO extends DAO[Project] {
  def getAll(): Seq[Project]
  def getById(projectId: UUID): Project
  def add(project: Project): Unit
}


// https://gist.github.com/davegurnell/4b432066b39949850b04
class ProjectDAOAnorm @Inject()(db: Database) extends ProjectDAO with Logging {

  val projectParser: RowParser[Project] = (
    SqlParser.get[UUID]("project.project_id") ~
      SqlParser.str("project.name") ~
      SqlParser.str("project.description") ~
      SqlParser.date("project.timestamp_created") ~
      SqlParser.date("project.timestamp_edited") ~
      SqlParser.bool("project.billable") ~
      SqlParser.get[UUID]("project.owned_by") ~
      SqlParser.get[UUID]("project.created_by") ~
      SqlParser.get[UUID]("project.last_edited_by") ~
      SqlParser.get[UUID]("project.client_id") ~
      SqlParser.str("client.name") ~
      SqlParser.str("client.email") ~
      SqlParser.date("client.timestamp_created") ~
      SqlParser.date("client.timestamp_edited")
    ) map {
    case projectId ~
      projectName ~
      projectDescription ~
      projectTsCreated ~
      projectTsEdited ~
      projectBillable ~
      ownedById ~
      createdById ~
      lastEditedById ~
      clientId ~
      clientName ~
      clientEmail ~
      clientTsCreated ~
      clientTsEdited
    => {
      val projectClient = Client(id = clientId, name = clientName)
      val projectOwner = User.byId(ownedById)
      val projectCreator = User.byId(createdById)
      val projectEditor = User.byId(lastEditedById)
      Project(id = projectId,
        name = projectName,
        description = projectDescription,
        owner = projectOwner,
        creator = projectCreator,
        client = projectClient,
        billable = projectBillable,
        creationTimestamp = projectTsCreated.getTime,
        lastEdited = projectTsEdited.getTime,
        lastEditor = projectEditor)
    }
  }

  def getAll(): Seq[Project] = db.withConnection {
      implicit c =>

        val allProjectsParser: ResultSetParser[List[Project]] = projectParser.*

        val projectResult: List[Project] = SQL("SELECT DISTINCT project.project_id, " +
          "project.name, project.description, " +
          "project.timestamp_created, project.timestamp_edited, " +
          "project.billable," +
          "project.owned_by, project.created_by, " +
          "project.last_edited_by, project.client_id," +
          "client.name, client.email, client.timestamp_created, " +
          "client.timestamp_edited " +
          "FROM project " +
          "INNER JOIN client ON (project.client_id = client.client_id);")
          .as(allProjectsParser)

        /* TODO: Add lists of managers and employees to the project
        TODO: Add linking tables for managers and employees to SQL
        val projectsWithLists: List[Project] = projectResult.map{project =>
          val project_id = project.id
          val projectEmployees: List[User] = SQL("SELECT u.* from ...").as[User]
        }

         */

        projectResult
    }
  def getById(projectId: UUID): Project = ???
  def add(project: Project): Unit = db.withConnection { implicit connection =>
    val sql =
      "INSERT INTO project (project_id, " +
        "name, " +
        "description, " +
        "timestamp_created, " +
        "timestamp_edited, " +
        "billable, " +
        "owned_by, " +
        "created_by," +
        "last_edited_by, " +
        "client_id) " +
        "values({project_id}::uuid, " +
        "{name}, " +
        "{description}, " +
        "CURRENT_TIMESTAMP, " +
        "CURRENT_TIMESTAMP, " +
        "{billable},  " +
        "{owned_by}::uuid, " +
        "{created_by}::uuid, " +
        "{last_edited_by}::uuid, " +
        "{client_id}::uuid);"
    logger.debug(s"ProjectDAOAnorm.add, SQL = $sql")
    SQL(sql).on("project_id" -> project.id,
      "name" -> project.name,
      "description" -> project.description,
    "billable" -> project.billable,
    "owned_by" -> project.owner.id,
    "created_by" -> project.creator.id,
    "last_edited_by" -> project.lastEditor.id,
    "client_id" -> project.client.id)
      .executeInsert(anorm.SqlParser.scalar[java.util.UUID].singleOpt)
  }
}
