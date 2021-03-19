package controllers

import models.ProjectRepository
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Logging
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

import javax.inject.Inject
import scala.reflect.ClassTag

object Inject {
  lazy val injector: Injector = (new GuiceApplicationBuilder).injector()
  def inject[T: ClassTag]: T  = injector.instanceOf[T]
}

class ProjectControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with Logging {

  val projectRepository: ProjectRepository = Inject.inject[ProjectRepository]
  val applicationJson                      = "application/json"

  "ProjectController GET" should {

    "return JSON data" in {
      val request  = FakeRequest(GET, "/projects")
      val projects = route(app, request).get

      status(projects) mustBe OK
      contentType(projects) mustBe Some("application/json")
    }

    // temporary test
    "return test data" in {
      val request  = FakeRequest(GET, "/projects")
      val projects = route(app, request).get

      status(projects) mustBe OK

    }

    "return projects by client id" in {
      val clientId = "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
      val getPath  = s"/clients/$clientId/projects"
      val requestClientProjects = FakeRequest(GET, getPath)
        .withHeaders("Content-type" -> "application/json")

      val response = route(app, requestClientProjects).get

      status(response) mustBe OK
      val responseBodyText = contentAsString(response)

      responseBodyText.contains("Testi_projekti") mustEqual true
      responseBodyText.contains("Väärä projekti") mustEqual false
    }

    "return users by project as JSON data" in {
      val usersByProject =
        "/projects/employees?projects=a3eb6db5-5212-46d0-bd08-8e852a45e0d3"

      logger.debug(
        s"""ProjectControllerSpec --> return users by project as JSON data, URL = $usersByProject"""
      )

      val usersRequest  = FakeRequest(GET, usersByProject)
      val usersResponse = route(app, usersRequest).get

      status(usersResponse) mustBe OK
      contentType(usersResponse) mustBe Some(applicationJson)
    }

    "return users by two projects as JSON data" in {
      val usersByProject =
        "/projects/employees?projects=a3eb6db5-5212-46d0-bd08-8e852a45e0d3&projects=a1eda1a6-a749-4932-9f48-16fe5b6a8ce9"

      logger.debug(
        s"""ProjectControllerSpec --> return users by two projects as JSON data, URL = $usersByProject"""
      )

      val usersRequest  = FakeRequest(GET, usersByProject)
      val usersResponse = route(app, usersRequest).get

      status(usersResponse) mustBe OK
      contentType(usersResponse) mustBe Some(applicationJson)
    }

  }

  "ProjectController POST" should {
    // TODO: create client and owner first
    "parse Project JSON correctly" in {
      val jsonString =
        """{"name": "Projekti",
          |"description": "kuvaus",
          |"client": "1bb44a7e-cd7c-447d-a9e9-26495b52fa88",
          |"owner": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"employees": [],
          |"billable": true
          }""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, "/projects")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }

    "reject invalid Project JSON" in {
      val jsonString = "{}"
      val json       = Json.parse(jsonString)

      val request = FakeRequest(POST, "/projects")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe BAD_REQUEST
    }

    "result in a Project being recorded and retrievable" in {
      val jsonString =
        """{"name": "Test Project",
          |"description": "Can this project be retrieved?",
          |"client": "1bb44a7e-cd7c-447d-a9e9-26495b52fa88",
          |"owner": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"employees": [],
          |"billable": true
          }""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, "/projects")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      val requestGet = FakeRequest(GET, "/projects")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)

      val resultGet = route(app, requestGet).get

      val bodyTextContainsPostedDescription =
        contentAsString(resultGet).contains("Can this project be retrieved?")

      bodyTextContainsPostedDescription mustEqual true
    }
  }

}
