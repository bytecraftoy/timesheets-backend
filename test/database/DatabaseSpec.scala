package database

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import anorm._
import anorm.SqlParser._
import play.api.db.Databases
import play.api.db.Database
import play.api.db.evolutions._

// https://www.playframework.com/documentation/2.8.x/ScalaTestingWithDatabases

class DatabaseSpec extends PlaySpec {

  def testDB[T](block: Database => T) = {
    Databases.withInMemory(
      name = "test",
      urlOptions = Map(
        "MODE" -> "PostgreSQL",
        "DATABASE_TO_UPPER" -> "false",
        "DB_CLOSE_DELAY" -> "-1"
      ),
      config = Map(
        "logStatements" -> true,
        "lazyInit" -> true,
        "username" -> "sa",
        "password" -> ""
      )
    ) { block }
  }

  "A database" should {
    "be able to be connected to" in {
      testDB { db =>
        db.withConnection(conn => 0)
      }

    }
  }
}
