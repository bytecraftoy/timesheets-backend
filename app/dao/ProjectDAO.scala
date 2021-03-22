package dao

import anorm.{ResultSetParser, RowParser, SQL, SqlParser}
import models.{Client, ClientRepository, Project, User, UserRepository}
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
  def update(project: Project): Unit
}

// https://gist.github.com/davegurnell/4b432066b39949850b04
class ProjectDAOAnorm @Inject() (
  db: Database,
  userRepo: UserRepository,
  clientRepo: ClientRepository
) extends ProjectDAO
    with Logging {

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
      SqlParser.get[UUID]("project.client_id")
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
        clientId => {
      val projectClient    = clientRepo.byId(clientId)
      val projectOwner     = userRepo.byId(ownedById)
      val projectManagers  = userRepo.getManagersByProjectId(projectId)
      val projectEmployees = userRepo.getEmployeesByProjectId(projectId)
      val projectCreator   = userRepo.byId(createdById)
      val projectEditor    = userRepo.byId(lastEditedById)
      Project(
        id = projectId,
        name = projectName,
        description = projectDescription,
        owner = projectOwner,
        creator = projectCreator,
        managers = projectManagers.toList,
        client = projectClient,
        employees = projectEmployees.toList,
        billable = projectBillable,
        creationTimestamp = projectTsCreated.getTime,
        lastEdited = projectTsEdited.getTime,
        lastEditor = projectEditor
      )
    }
  }
  val allProjectsParser: ResultSetParser[List[Project]] = projectParser.*

  def getAll(): Seq[Project] =
    db.withConnection { implicit c =>
      val allProjectsParser: ResultSetParser[List[Project]] = projectParser.*
      val projectResult: List[Project] = SQL(
        "SELECT DISTINCT project.project_id, " +
          "project.name, upper(project.name) as project_upper, project.description, " +
          "project.timestamp_created, project.timestamp_edited, " +
          "project.billable," +
          "project.owned_by, project.created_by, " +
          "project.last_edited_by, project.client_id," +
          "client.name, client.email, client.timestamp_created, " +
          "client.timestamp_edited " +
          "FROM project " +
          "INNER JOIN client ON (project.client_id = client.client_id) " +
          "ORDER BY project_upper ASC ;"
      ).as(allProjectsParser)
      projectResult
    }

  def getById(projectId: UUID): Project =
    db.withConnection { implicit c =>
      val sql =
        "SELECT project_id, " +
          "name, " +
          "description, " +
          "timestamp_created, " +
          "timestamp_edited, " +
          "billable, " +
          "owned_by, " +
          "created_by, " +
          "last_edited_by, " +
          "client_id" +
          " FROM project " +
          "WHERE project_id = {projectId}::uuid;"
      logger.debug(s"ProjectDAOAnorm.getById(), SQL = $sql")

      val results = SQL(sql)
        .on("projectId" -> projectId)
        .as(allProjectsParser)
      if (results.isEmpty) {
        null
      } else {
        results.head
      }
    }

  def add(project: Project): Unit = {
    db.withConnection { implicit connection =>
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
      SQL(sql)
        .on(
          "project_id"     -> project.id,
          "name"           -> project.name,
          "description"    -> project.description,
          "billable"       -> project.billable,
          "owned_by"       -> project.owner.id,
          "created_by"     -> project.creator.id,
          "last_edited_by" -> project.lastEditor.id,
          "client_id"      -> project.client.id
        )
        .executeInsert(anorm.SqlParser.scalar[java.util.UUID].singleOpt)
    }
    project.employees.foreach(
      (employee) => userRepo.addUserToProject(employee.id, project.id)
    )
  }

  def update(project: Project): Unit =
    db.withConnection { implicit c =>
      val sql =
        "UPDATE project SET (name, " +
          "description, " +
          "billable, " +
          "owned_by, " +
          "client_id, " +
          "last_edited_by, " +
          "timestamp_edited)" +
          " = ({name}, " +
          "{description}, " +
          "{billable}, " +
          "{owned_by}::uuid, " +
          "{client_id}::uuid, " +
          "{last_edited_by}::uuid, " +
          "CURRENT_TIMESTAMP)" +
          " WHERE project_id = {id}::uuid;"
      logger.debug(s"ProjectDAOAnorm.update, SQL = $sql")
      val result: Int = SQL(sql)
        .on(
          "id"             -> project.id,
          "name"           -> project.name,
          "description"    -> project.description,
          "billable"       -> project.billable,
          "owned_by"       -> project.owner.id,
          "last_edited_by" -> project.lastEditor.id,
          "client_id"      -> project.client.id
        )
        .executeUpdate()

      logger.debug(s"""ProjectDAOAnorm.update, updated $result rows.""")

      val savedEmployees = userRepo.getEmployeesByProjectId(project.id)

      val removedEmployees =
        savedEmployees.filterNot(project.employees.contains(_))
      val newEmployees = project.employees.filterNot(savedEmployees.contains(_))

      removedEmployees.foreach(
        (employee) => userRepo.removeUserFromProject(employee.id, project.id)
      )
      newEmployees.foreach(
        (employee) => userRepo.addUserToProject(employee.id, project.id)
      )
      result
    }
}
