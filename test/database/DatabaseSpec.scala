package database

import akka.actor.TypedActor.dispatcher
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import anorm._
import anorm.SqlParser._
import play.api.db.Databases
import play.api.db.Database
import play.api.db.evolutions._
import play.api.mvc.Results.Ok
import javax.inject.Inject
import scala.concurrent.Future

import play.api.inject.Injector
import play.api.mvc.Results.Ok
import javax.inject.Inject
import scala.concurrent.Future

import scala.reflect.ClassTag
import play.api.inject.guice.GuiceApplicationBuilder


// https://www.playframework.com/documentation/2.8.x/ScalaTestingWithDatabases

// https://stackoverflow.com/questions/34159857/specs2-how-to-test-a-class-with-more-than-one-injected-dependency
// Play's default test framework does not play well with Play's default DI. Therefore this helper object:
object Inject {
  lazy val injector: Injector = (new GuiceApplicationBuilder).injector()
  def inject[T: ClassTag]: T = injector.instanceOf[T]
}

class DatabaseSpec extends PlaySpec {

  // Inject app database configuration.
  val appDatabase: Database = Inject.inject[Database]

  def testDB[T](block: Database => T): T=
    Databases.withInMemory(
      name = "test",
      urlOptions = Map(
        "MODE"              -> "PostgreSQL",
        "DATABASE_TO_UPPER" -> "false",
        "DB_CLOSE_DELAY"    -> "-1"
      ),
      config = Map(
        "logStatements"   -> true,
        "lazyInit"        -> true,
        "username"        -> "sa",
        "password"        -> "",
        "evolutionplugin" -> "enabled",
        "applyEvolutions" -> true
      )
    ) { database =>
      Evolutions.withEvolutions(database) { block(database) }
    }

  val appUserParser: RowParser[String] = (
    SqlParser.str("first_name") ~
      SqlParser.str("last_name") ~
      SqlParser.bool("is_manager") ~
      SqlParser.date("timestamp_created") ~
      SqlParser.date("timestamp_edited")
  ) map {
    case first_name ~ last_name ~ is_manager ~ ts_created ~ ts_edited =>
      first_name + " " +
        last_name +
        ", manager: " +
        is_manager.toString +
        ", creation: " +
        ts_created.toString + "" +
        ", edit: " +
        ts_edited.toString
  }

  val allAppUsersParser: ResultSetParser[List[String]] = appUserParser.*

  val projectParser: RowParser[String] = (
    SqlParser.str("name") ~
      SqlParser.str("description") ~
      SqlParser.date("timestamp_created") ~
      SqlParser.date("timestamp_edited") ~
      SqlParser.bool("billable") ~
      SqlParser.long("owned_by") ~
      SqlParser.long("created_by") ~
      SqlParser.long("last_edited_by") ~
      SqlParser.long("client_id")
  ) map {
    case name ~
          description ~
          ts_created ~
          ts_edited ~
          billable ~
          owned_by ~
          created_by ~
          last_edited_by ~
          client_id => ("%s, " +
                        "description: %s, " +
                        "created: %s, " +
                        "edited: %s, " +
      "billable: %s, " +
      "owner_id: %s, " +
      "creator_id: %s, " +
      "last_editor_id: %s, " +
      "client_id: %s ")
      .format(name,
        description,
        ts_created.toString,
        ts_edited.toString,
        billable.toString,
        owned_by.toString,
        created_by.toString,
        last_edited_by.toString,
        client_id.toString)
  }

  val allProjectsParser: ResultSetParser[List[String]] = projectParser.*

  "A database" should {
    "be able to be connected to" in {
      testDB { test_db =>
        test_db.withConnection(conn => 0)
      }
    }

    "have a table project" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val result: Boolean = SQL("SELECT * FROM project")
            .execute()

          result mustBe true
        }
      }
    }

    "not have a table projects" in {
      val thrown = intercept[Exception] {
        testDB { test_db =>
          test_db.withConnection { implicit conn =>
            try {
              val result: Boolean = SQL("SELECT * FROM projects")
                .execute()
            } catch {
              case notFound: Exception => throw new Exception("Not found")
            }
          }
        }
      }
      thrown.getMessage mustBe "Not found"
    }

    "have the ability to add an appUser" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[Long] =
            SQL("INSERT INTO app_user(first_name, " +
                "last_name," +
                "is_manager," +
                "timestamp_created," +
                "timestamp_edited)" +
                "values({first_name}," +
                "{last_name}," +
                "{is_manager}," +
                "{timestamp_created}," +
                "{timestamp_edited})"
            ).on(
                "first_name"        -> "Koli",
                "last_name"         -> "Sukunimi3",
                "is_manager"        -> "TRUE",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10"
              )
              .executeInsert()
          val expectType: Long = 1

          id.get.getClass mustBe expectType.getClass
        }
      }
    }

    "have the ability to add a project row" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[Long] =
            SQL(
              "INSERT INTO project(name, " +
                "description," +
                "timestamp_created," +
                "timestamp_edited," +
                "billable," +
                "owned_by," +
                "created_by," +
                "last_edited_by," +
                "client_id)" +
                "values({name}," +
                "{description}," +
                "{timestamp_created}," +
                "{timestamp_edited}," +
                "{billable}," +
                "{owned_by}," +
                "{created_by}," +
                "{last_edited_by}," +
                "{client_id})"
            ).on(
                "name"              -> "Testiprojekti",
                "description"       -> "Luotu testausta varten",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10",
                "billable"          -> "TRUE",
                "owned_by"          -> "1",
                "created_by"        -> "1",
                "last_edited_by"    -> "1",
                "client_id"  -> "1"
              )
              .executeInsert()
          val expectType: Long = 1
          id.get.getClass mustBe expectType.getClass
        }
      }
    }

    "return all projects" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[Long] =
            SQL(
              "INSERT INTO project(name, " +
                "description," +
                "timestamp_created," +
                "timestamp_edited," +
                "billable," +
                "owned_by," +
                "created_by," +
                "last_edited_by," +
                "client_id)" +
                "values({name}," +
                "{description}," +
                "{timestamp_created}," +
                "{timestamp_edited}," +
                "{billable}," +
                "{owned_by}," +
                "{created_by}," +
                "{last_edited_by}," +
                "{client_id})"
            ).on(
                "name"              -> "Testiprojekti",
                "description"       -> "Luotu testausta varten",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10",
                "billable"          -> "TRUE",
                "owned_by"          -> "1",
                "created_by"        -> "1",
                "last_edited_by"    -> "1",
                "client_id"  -> "1"
              )
              .executeInsert()
          val projectList: List[String] = SQL("SELECT * FROM project")
            .as(allProjectsParser)
          projectList.head.contains("Testi_projekti") mustBe true
          projectList(1).contains("Testiprojekti") mustBe true
        }
      }
    }

    "not add a project without an owner" in {
      val thrown = intercept[Exception] {
        testDB { test_db =>
          test_db.withConnection { implicit conn =>
            try {
              val id: Option[Long] =
                SQL(
                  "INSERT INTO project(name, " +
                    "description," +
                    "timestamp_created," +
                    "timestamp_edited," +
                    "billable," +
                    "created_by," +
                    "last_edited_by," +
                    "client_id)" +
                    "values({name}," +
                    "{description}," +
                    "{timestamp_created}," +
                    "{timestamp_edited}," +
                    "{billable}," +
                    "{created_by}," +
                    "{last_edited_by}," +
                    "{cliet_id})"
                ).on(
                  "name" -> "Testiprojekti",
                  "description" -> "Luotu testausta varten",
                  "timestamp_created" -> "2014-11-21 04:25:10",
                  "timestamp_edited" -> "2014-11-21 04:25:10",
                  "billable" -> "TRUE",
                  "created_by" -> "1",
                  "last_edited_by" -> "1",
                  "client_id" -> "1"
                )
                  .executeInsert()
            } catch {
              case notAllowed: Exception => throw new Exception("Not allowed")
            }
          }
        }
      }
      thrown.getMessage mustBe "Not allowed"
    }

    "not add a project with an existing id" in {
      val thrown = intercept[Exception] {
        testDB { test_db =>
          test_db.withConnection { implicit conn =>
            try {
              val id: Option[Long] =
                SQL(
                  "INSERT INTO Project(project_id," +
                    "name, " +
                    "description," +
                    "timestamp_created," +
                    "timestamp_edited," +
                    "billable," +
                    "owned_by," +
                    "created_by," +
                    "last_edited_by," +
                    "client_id)" +
                    "values({project_id}," +
                    "{name}," +
                    "{description}," +
                    "{timestamp_created}," +
                    "{timestamp_edited}," +
                    "{billable}," +
                    "{owned_by}" +
                    "{created_by}," +
                    "{last_edited_by}," +
                    "{client_id})"
                ).on(
                  "project_id" -> "1",
                  "name" -> "Testiprojekti",
                  "description" -> "Luotu testausta varten",
                  "timestamp_created" -> "2014-11-21 04:25:10",
                  "timestamp_edited" -> "2014-11-21 04:25:10",
                  "billable" -> "TRUE",
                  "owned_by" -> "1",
                  "created_by" -> "1",
                  "last_edited_by" -> "1",
                  "client_id" -> "1"
                )
                  .executeInsert()
            } catch {
              case notAllowed: Exception => throw new Exception("Not allowed")
            }
          }
        }
      }
      thrown.getMessage mustBe "Not allowed"
    }

  }


  "The app database timesheet" should {
    "have tables project, client, and app_user" in {
      Evolutions.withEvolutions(appDatabase)(appDatabase.withConnection {
        implicit c =>
          val projectResult: Boolean = SQL("Select * from project").execute()
          projectResult mustBe true
          val appUserResult: Boolean = SQL("Select * from app_user").execute()
          appUserResult mustBe true
          val clientResult: Boolean = SQL("Select * from client").execute()
          clientResult mustBe true
      })
    }
    "have the name timesheet" in {
      Evolutions.withEvolutions(appDatabase)(appDatabase.withConnection {
        implicit c =>
          println("Database url: " + appDatabase.url)
          println("data source: " + appDatabase.dataSource)
          appDatabase.name mustBe "timesheet"
      })
    }

  }

}