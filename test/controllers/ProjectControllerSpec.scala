package controllers

import models.ProjectRepository
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
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
  def inject[T: ClassTag]: T = injector.instanceOf[T]
}

class ProjectControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  val projectRepository: ProjectRepository = Inject.inject[ProjectRepository]
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
  }

  "ProjectController POST" should {
    // TODO: create client and owner first
    "parse Project JSON correctly" in {
      val jsonString =
        """{"name": "Projekti",
          |"description": "kuvaus",
          |"client": "1bb44a7e-cd7c-447d-a9e9-26495b52fa88",
          |"owner": "9fa407f4-7375-446b-92c6-c578839b7780",
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
      val json = Json.parse(jsonString)

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

      val bodyTextContainsPostedDescription = contentAsString(resultGet).contains("Can this project be retrieved?")

      bodyTextContainsPostedDescription mustEqual true
    }
  }

}
