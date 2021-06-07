package persistence.dao

import anorm.{ResultSetParser, RowParser, SQL, SqlParser, _}
import com.google.inject.ImplementedBy
import domain.models.{HourlyCost, Project}
import domain.services.{ClientRepository, UserRepository}
import play.api.Logging
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[ProjectDAOAnorm])
trait ProjectDAO extends DAO[Project] {
  def getAll: Seq[Project]
  def getById(projectId: UUID): Option[Project]
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
      SqlParser.get[UUID]("project.client_id") ~
      SqlParser.get[Option[BigDecimal]]("project.hourly_cost") ~
      SqlParser.get[Option[String]]("project.currency")
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
        hourlyCost ~
        currency => {
      val projectClient    = clientRepo.byId(clientId).get // TODO: avoid calling get
      val projectOwner     = userRepo.byId(ownedById).get // TODO: avoid calling get
      val projectManagers  = userRepo.getManagersByProjectId(projectId)
      val projectEmployees = userRepo.getEmployeesByProjectId(projectId)
      val projectCreator   = userRepo.byId(createdById).get // TODO: avoid calling get
      val projectEditor    = userRepo.byId(lastEditedById).get // TODO: avoid calling get
      Project(
        id = projectId,
        name = projectName,
        description = projectDescription,
        owner = projectOwner,
        createdBy = projectCreator,
        managers = projectManagers.toList,
        client = projectClient,
        employees = projectEmployees.toList,
        billable = projectBillable,
        created = projectTsCreated.getTime,
        edited = projectTsEdited.getTime,
        editedBy = projectEditor,
        hourlyCost =
          HourlyCost(hourlyCost getOrElse BigDecimal(0), currency getOrElse "")
      )
    }
  }
  val allProjectsParser: ResultSetParser[List[Project]] = projectParser.*

  def getAll: Seq[Project] =
    db.withConnection { implicit c =>
      val allProjectsParser: ResultSetParser[List[Project]] = projectParser.*
      val projectResult: List[Project] = SQL(
        "SELECT DISTINCT project.project_id, " +
          "project.name, upper(project.name) as project_upper, project.description, " +
          "project.timestamp_created, project.timestamp_edited, " +
          "project.billable," +
          "project.owned_by, project.created_by, " +
          "project.last_edited_by, project.client_id," +
          "project.hourly_cost, project.currency," +
          "client.name, client.email, client.timestamp_created, " +
          "client.timestamp_edited " +
          "FROM project " +
          "INNER JOIN client ON (project.client_id = client.client_id) " +
          "ORDER BY project_upper ASC ;"
      ).as(allProjectsParser)
      projectResult
    }

  def getById(projectId: UUID): Option[Project] =
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
          "client_id," +
          "hourly_cost," +
          "currency" +
          " FROM project " +
          "WHERE project_id = {projectId}::uuid;"
      logger.debug(s"ProjectDAOAnorm.getById(), SQL = $sql")

      val results = SQL(sql)
        .on("projectId" -> projectId)
        .as(allProjectsParser)
      results.headOption
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
          "created_by, " +
          "last_edited_by, " +
          "client_id, " +
          "hourly_cost, " +
          "currency) " +
          "values({project_id}::uuid, " +
          "{name}, " +
          "{description}, " +
          "CURRENT_TIMESTAMP, " +
          "CURRENT_TIMESTAMP, " +
          "{billable},  " +
          "{owned_by}::uuid, " +
          "{created_by}::uuid, " +
          "{last_edited_by}::uuid, " +
          "{client_id}::uuid, " +
          "{hourly_cost}, " +
          "{currency});"
      logger.debug(s"ProjectDAOAnorm.add, SQL = $sql")
      SQL(sql)
        .on(
          "project_id"     -> project.id,
          "name"           -> project.name,
          "description"    -> project.description,
          "billable"       -> project.billable,
          "owned_by"       -> project.owner.id,
          "created_by"     -> project.createdBy.id,
          "last_edited_by" -> project.editedBy.id,
          "client_id"      -> project.client.id,
          "hourly_cost"    -> project.hourlyCost.value,
          "currency"       -> project.hourlyCost.currency
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
          "timestamp_edited, " +
          "hourly_cost, " +
          "currency)" +
          " = ({name}, " +
          "{description}, " +
          "{billable}, " +
          "{owned_by}::uuid, " +
          "{client_id}::uuid, " +
          "{last_edited_by}::uuid, " +
          "CURRENT_TIMESTAMP, " +
          "{hourly_cost}, " +
          "{currency})" +
          " WHERE project_id = {id}::uuid;"
      logger.debug(s"ProjectDAOAnorm.update, SQL = $sql")
      val result: Int = SQL(sql)
        .on(
          "id"             -> project.id,
          "name"           -> project.name,
          "description"    -> project.description,
          "billable"       -> project.billable,
          "owned_by"       -> project.owner.id,
          "last_edited_by" -> project.editedBy.id,
          "client_id"      -> project.client.id,
          "hourly_cost"    -> project.hourlyCost.value,
          "currency"       -> project.hourlyCost.currency
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
