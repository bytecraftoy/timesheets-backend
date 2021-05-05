package database

import anorm.SqlParser._
import anorm._
import org.scalatestplus.play._
import play.api.db.{Database, Databases}
import play.api.db.evolutions._
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder

import java.util.UUID
import scala.reflect.ClassTag

// https://www.playframework.com/documentation/2.8.x/ScalaTestingWithDatabases

// https://stackoverflow.com/questions/34159857/specs2-how-to-test-a-class-with-more-than-one-injected-dependency
// Play's default test framework does not play well with Play's default DI. Therefore this helper object:
object Inject {
  lazy val injector: Injector = (new GuiceApplicationBuilder).injector()
  def inject[T: ClassTag]: T  = injector.instanceOf[T]
}

class DatabaseSpec extends PlaySpec {

  // Inject app database configuration.
  val appDatabase: Database = Inject.inject[Database]

  def testDB[T](block: Database => T): T =
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
    SqlParser.get[UUID]("app_user_id") ~
      SqlParser.str("username") ~
      SqlParser.str("first_name") ~
      SqlParser.str("last_name") ~
      SqlParser.bool("is_manager") ~
      SqlParser.date("timestamp_created") ~
      SqlParser.date("timestamp_edited")
  ) map {
    case app_user_id ~ username ~ first_name ~ last_name ~ is_manager ~ ts_created ~ ts_edited =>
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
    SqlParser.get[UUID]("project_id") ~
      SqlParser.str("name") ~
      SqlParser.str("description") ~
      SqlParser.date("timestamp_created") ~
      SqlParser.date("timestamp_edited") ~
      SqlParser.bool("billable") ~
      SqlParser.get[UUID]("owned_by") ~
      SqlParser.get[UUID]("created_by") ~
      SqlParser.get[UUID]("last_edited_by") ~
      SqlParser.get[UUID]("client_id")
  ) map {
    case project_id ~
        name ~
        description ~
        ts_created ~
        ts_edited ~
        billable ~
        owned_by ~
        created_by ~
        last_edited_by ~
        client_id =>
      ("id: %s, " +
        "name: %s," +
        "description: %s, " +
        "created: %s, " +
        "edited: %s, " +
        "billable: %s, " +
        "owner_id: %s, " +
        "creator_id: %s, " +
        "last_editor_id: %s, " +
        "client_id: %s ")
        .format(
          project_id.toString,
          name,
          description,
          ts_created.toString,
          ts_edited.toString,
          billable.toString,
          owned_by.toString,
          created_by.toString,
          last_edited_by.toString,
          client_id.toString
        )
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

    // insert UUIDs https://stackoverflow.com/questions/30985604/how-to-insert-value-of-uuid
    "have the ability to add an appUser" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[UUID] =
            SQL(
              "INSERT INTO app_user(app_user_id," +
                "username, " +
                "first_name, " +
                "last_name," +
                "is_manager," +
                "timestamp_created," +
                "timestamp_edited)" +
                "values({app_user_id}::uuid," +
                "{username}, " +
                "{first_name}," +
                "{last_name}," +
                "{is_manager}," +
                "{timestamp_created}," +
                "{timestamp_edited})"
            ).on(
                "app_user_id"       -> "e17ed08e-91f5-43c4-84e8-3b2ac07e605d",
                "username"          -> "my user name",
                "first_name"        -> "Koli",
                "last_name"         -> "Sukunimi3",
                "is_manager"        -> "TRUE",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10"
              )
              .executeInsert(scalar[UUID].singleOpt)
        }
      }
    }

    "have the ability to add a project row" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[UUID] =
            SQL(
              "INSERT INTO project(project_id," +
                "name, " +
                "description," +
                "timestamp_created," +
                "timestamp_edited," +
                "billable," +
                "owned_by," +
                "created_by," +
                "last_edited_by," +
                "client_id)" +
                "values({project_id}::uuid," +
                "{name}," +
                "{description}," +
                "{timestamp_created}," +
                "{timestamp_edited}," +
                "{billable}," +
                "{owned_by}::uuid," +
                "{created_by}::uuid," +
                "{last_edited_by}::uuid," +
                "{client_id}::uuid)"
            ).on(
                "project_id"        -> "0b940f80-bff8-48c1-8270-483ea223e2e5",
                "name"              -> "Testiprojekti",
                "description"       -> "Luotu testausta varten",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10",
                "billable"          -> "TRUE",
                "owned_by"          -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "created_by"        -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "last_edited_by"    -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "client_id"         -> "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
              )
              .executeInsert(scalar[UUID].singleOpt)
        }
      }
    }

    "return all projects" in {
      testDB { test_db =>
        test_db.withConnection { implicit conn =>
          val id: Option[UUID] =
            SQL(
              "INSERT INTO project(project_id," +
                "name, " +
                "description," +
                "timestamp_created," +
                "timestamp_edited," +
                "billable," +
                "owned_by," +
                "created_by," +
                "last_edited_by," +
                "client_id)" +
                "values({project_id}::uuid," +
                "{name}," +
                "{description}," +
                "{timestamp_created}," +
                "{timestamp_edited}," +
                "{billable}," +
                "{owned_by}::uuid," +
                "{created_by}::uuid," +
                "{last_edited_by}::uuid," +
                "{client_id}::uuid)"
            ).on(
                "project_id"        -> "92440b0b-62d6-499f-bc27-4931bb3fa344",
                "name"              -> "Testiprojekti",
                "description"       -> "Luotu testausta varten",
                "timestamp_created" -> "2014-11-21 04:25:10",
                "timestamp_edited"  -> "2014-11-21 04:25:10",
                "billable"          -> "TRUE",
                "owned_by"          -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "created_by"        -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "last_edited_by"    -> "9fa407f4-7375-446b-92c6-c578839b7780",
                "client_id"         -> "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
              )
              .executeInsert(scalar[UUID].singleOpt)
          val projectList: List[String] = SQL("SELECT * FROM project")
            .as(allProjectsParser)

          var test1, test2: Boolean = false

          var p: String = ""
          for (p <- projectList) {
            if (p.contains("Testi_projekti")) {
              test1 = true
            }
            if (p.contains("Testiprojekti")) {
              test2 = true
            }
          }

          test1 mustBe true
          test2 mustBe true
        }
      }
    }

    "not add a project without an owner" in {
      val thrown = intercept[Exception] {
        testDB { test_db =>
          test_db.withConnection { implicit conn =>
            try {
              val id: UUID =
                SQL(
                  "INSERT INTO project(project_id," +
                    "name, " +
                    "description," +
                    "timestamp_created," +
                    "timestamp_edited," +
                    "billable," +
                    "created_by," +
                    "last_edited_by," +
                    "client_id)" +
                    "values({project_id}::uuid," +
                    "{name}," +
                    "{description}," +
                    "{timestamp_created}," +
                    "{timestamp_edited}," +
                    "{billable}," +
                    "{created_by}::uuid," +
                    "{last_edited_by}::uuid," +
                    "{client_id}::uuid)"
                ).on(
                    "project_id"        -> "9eedef96-d8d3-401b-b5eb-cf61d8d61f63",
                    "name"              -> "Testiprojekti",
                    "description"       -> "Luotu testausta varten",
                    "timestamp_created" -> "2014-11-21 04:25:10",
                    "timestamp_edited"  -> "2014-11-21 04:25:10",
                    "billable"          -> "TRUE",
                    "created_by"        -> "fa407f4-7375-446b-92c6-c578839b7780",
                    "last_edited_by"    -> "fa407f4-7375-446b-92c6-c578839b7780",
                    "client_id"         -> "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
                  )
                  .executeInsert(scalar[UUID].single)
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
              val id: UUID =
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
                    "values({project_id}::uuid," +
                    "{name}," +
                    "{description}," +
                    "{timestamp_created}," +
                    "{timestamp_edited}," +
                    "{billable}," +
                    "{owned_by}::uuid," +
                    "{created_by}::uuid," +
                    "{last_edited_by}::uuid," +
                    "{client_id}::uuid)"
                ).on(
                    "project_id"        -> "a3eb6db5-5212-46d0-bd08-8e852a45e0d3",
                    "name"              -> "Testiprojekti",
                    "description"       -> "Luotu testausta varten",
                    "timestamp_created" -> "2014-11-21 04:25:10",
                    "timestamp_edited"  -> "2014-11-21 04:25:10",
                    "billable"          -> "TRUE",
                    "owned_by"          -> "9fa407f4-7375-446b-92c6-c578839b7780",
                    "created_by"        -> "9fa407f4-7375-446b-92c6-c578839b7780",
                    "last_edited_by"    -> "9fa407f4-7375-446b-92c6-c578839b7780",
                    "client_id"         -> "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
                  )
                  .executeInsert(scalar[UUID].single)
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
