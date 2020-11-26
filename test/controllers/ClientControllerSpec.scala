package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

class ClientControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ClientController GET" should {

    "return JSON data" in {
      val request  = FakeRequest(GET, "/clients")
      val projects = route(app, request).get

      status(projects) mustBe OK
      contentType(projects) mustBe Some("application/json")
    }
  }
}
