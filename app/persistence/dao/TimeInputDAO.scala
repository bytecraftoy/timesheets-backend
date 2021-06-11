package persistence.dao

import anorm.{ResultSetParser, _}
import com.google.inject.ImplementedBy
import domain.models.TimeInput
import domain.services.{ProjectRepository, UserRepository}
import play.api.Logging
import play.api.db.Database

import java.time.{Instant, LocalDate, ZoneId}
import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[TimeInputDAOAnorm])
trait TimeInputDAO extends DAO[TimeInput] {
  def getAll(): Seq[TimeInput]
  def getById(timeInputId: UUID): Option[TimeInput]
  def add(timeInput: TimeInput): Unit
  def update(timeInput: TimeInput): Int
  def byProject(projectId: UUID): Seq[TimeInput]
  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput]
  def byProjectAndEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput]
  def byEmployeeInterval(
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput]
}

// https://gist.github.com/davegurnell/4b432066b39949850b04
class TimeInputDAOAnorm @Inject() (
  db: Database,
  userRepository: UserRepository,
  projectRepository: ProjectRepository
) extends TimeInputDAO
    with Logging {

  def getAll(): Seq[TimeInput] = {
    val sql = SQL("SELECT * FROM timeinput;")
    getTimeInputs(sql)
  }

  def byProject(projectId: UUID): Seq[TimeInput] = {
    val sql = SQL(
      "SELECT * FROM timeinput " +
        "WHERE project_id = {projectId}::uuid " +
        "ORDER BY app_user_id ASC, input_date ASC;"
    ).on("projectId" -> projectId)
    getTimeInputs(sql)
  }

  def byEmployee(employeeId: UUID): Seq[TimeInput] = {
    val sql = SQL(
      "SELECT * FROM timeinput " +
        "WHERE app_user_id = {employeeId}::uuid " +
        "ORDER BY project_id ASC, input_date ASC;"
    ).on("employeeId" -> employeeId)
    getTimeInputs(sql)
  }

  def byEmployeeInterval(
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput] = {

    if (start == LocalDate.MIN && end == LocalDate.MAX) {
      logger.debug(
        "Calling TimeInputDAOAnorm.byEmployee() instead. " +
          "Dates are start MIN and end MAX."
      )
      byEmployee(employeeId)
    } else {

      val sql = SQL(
        "SELECT * FROM timeinput " +
          "WHERE app_user_id = {employeeId}::uuid " +
          "AND input_date >= {start} AND input_date <= {end} " +
          "ORDER BY project_id ASC, input_date ASC;"
      ).on("employeeId" -> employeeId, "start" -> start, "end" -> end)

      getTimeInputs(sql)
    }
  }

  def byProjectAndEmployee(
    projectId: UUID,
    employeeId: UUID
  ): Seq[TimeInput] = {
    val sql = SQL(
      "SELECT * FROM timeinput WHERE project_id = {projectId}::uuid " +
        "AND app_user_id = {employeeId}::uuid ORDER BY input_date ASC;"
    ).on("projectId" -> projectId, "employeeId" -> employeeId)

    getTimeInputs(sql)
  }

  def byProjectAndEmployeeInterval(
    projectId: UUID,
    employeeId: UUID,
    start: LocalDate,
    end: LocalDate
  ): Seq[TimeInput] = {

    if (start == LocalDate.MIN && end == LocalDate.MAX) {
      logger.debug(
        "Calling TimeInputDAOAnorm.byProjectAndEmployee() instead. " +
          "Dates are start MIN and end MAX."
      )
      byProjectAndEmployee(projectId, employeeId)
    } else {

      val sql = SQL(
        "SELECT * FROM timeinput WHERE project_id = {projectId}::uuid " +
          "AND app_user_id = {employeeId}::uuid " +
          "AND input_date >= {start} " +
          "AND input_date <= {end} ORDER BY input_date ASC;"
      ).on(
        "projectId"  -> projectId,
        "employeeId" -> employeeId,
        "start"      -> start,
        "end"        -> end
      )

      getTimeInputs(sql)
    }
  }

  def byTimeInterval(start: LocalDate, end: LocalDate): Seq[TimeInput] = {
    val sql = SQL(
      "SELECT * FROM timeinput " +
        "WHERE input_date >= {start} " +
        "AND input_date <= {end} " +
        "ORDER BY app_user_id ASC, project_id ASC, input_date ASC;"
    ).on("start" -> start, "end" -> end)

    getTimeInputs(sql)
  }

  def getById(timeInputId: UUID): Option[TimeInput] = {
    val sql = SQL(
      "SELECT * FROM timeinput " +
        "WHERE timeinput_id = {timeInputId}::uuid ;"
    ).on("timeInputId" -> timeInputId)

    val results = getTimeInputs(sql)
    results.headOption
  }

  def add(timeInput: TimeInput): Unit = {
    db.withConnection { implicit connection =>
      val sql =
        "INSERT INTO timeinput (timeinput_id, " +
          "app_user_id, " +
          "project_id, " +
          "input_date, " +
          "minutes, " +
          "description, " +
          "timestamp_created, " +
          "timestamp_edited) " +
          "VALUES ({id}::uuid, " +
          "{employee.id}::uuid, " +
          "{project.id}::uuid, " +
          "{date}, " +
          "{input}, " +
          "{description}, " +
          "CURRENT_TIMESTAMP, " +
          "CURRENT_TIMESTAMP);"
      logger.debug(s"TimeInputDAOAnorm.add, SQL = $sql")
      SQL(sql)
        .on(
          "id"          -> timeInput.id,
          "employee.id" -> timeInput.employee.id,
          "project.id"  -> timeInput.project.id,
          "date"        -> timeInput.date,
          "input"       -> timeInput.input,
          "description" -> timeInput.description
        )
        .executeInsert(anorm.SqlParser.scalar[java.util.UUID].singleOpt)
    }
  }

  def update(timeInput: TimeInput): Int = {
    db.withConnection { implicit connection =>
      val sql =
        "UPDATE timeinput SET (minutes, description, timestamp_edited)" +
          " = ({input}, {description}, CURRENT_TIMESTAMP)" +
          " WHERE timeinput_id = {id}::uuid ;"
      logger.debug(s"""TimeInputDAOAnorm.update, SQL = $sql""")
      val result: Int = SQL(sql)
        .on(
          "id"          -> timeInput.id,
          "input"       -> timeInput.input,
          "description" -> timeInput.description
        )
        .executeUpdate()

      logger.debug(s"""TimeInputDAOAnorm.update, updated $result rows.""")

      result
    }
  }

  def getTimeInputs(sql: SimpleSql[Row]): Seq[TimeInput] =
    db.withConnection { implicit c =>
      val timeInputRowParser: RowParser[TimeInput] = (
        SqlParser.get[UUID]("timeinput_id") ~
          SqlParser.get[UUID]("app_user_id") ~
          SqlParser.get[UUID]("project_id") ~
          SqlParser.date("input_date") ~
          SqlParser.long("minutes") ~
          SqlParser.get[Option[String]]("description") ~
          SqlParser.date("timestamp_created") ~
          SqlParser.date("timestamp_edited")
      ) map {
        case timeinput_id ~ app_user_id ~ project_id ~ input_date ~ minutes ~ description ~ timestamp_created ~ timestamp_edited =>
          TimeInput(
            id = timeinput_id,
            employee = userRepository.byId(app_user_id).get, // TODO: avoid calling get
            project = projectRepository.byId(project_id).get, // TODO: avoid calling get
            date = Instant
              .ofEpochMilli(input_date.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate(),
            input = minutes,
            description = description.getOrElse(""),
            created = timestamp_created.getTime(),
            edited = timestamp_edited.getTime()
          )
      }

      val timeInputParser: ResultSetParser[List[TimeInput]] =
        timeInputRowParser.*
      val timeInputResult: List[TimeInput] = sql.as(timeInputParser)
      logger.debug(s"For SQL: ${sql}")
      logger.debug(
        "TimeInputDAO.getTimeInputs() parsed " + timeInputResult.size + " result rows."
      )
      timeInputResult
    }
}
