package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

class ProjectControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ProjectController GET" should {

    "return JSON data" in {
      val request  = FakeRequest(GET, "/projects")
      val projects = route(app, request).get

      status(projects) mustBe OK
      contentType(projects) mustBe Some("application/json")
    }
  }

  "ProjectController POST" should {

    "parse Project JSON correctly" in {
      val jsonString =
        """{"name": "Projekti",
          |"description": "kuvaus",
          |"client": 1,
          |"owner": 1,
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
  }

}
