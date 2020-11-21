package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

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
}
