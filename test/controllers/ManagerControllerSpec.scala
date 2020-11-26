package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class ManagerControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ManagerController GET" should {

    "return JSON data" in {
      val request  = FakeRequest(GET, "/managers")
      val projects = route(app, request).get

      status(projects) mustBe OK
      contentType(projects) mustBe Some("application/json")
    }
  }
}
