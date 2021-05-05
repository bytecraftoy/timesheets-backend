package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

import java.time.Clock

class ClientControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ClientController GET" should {

    "return JSON data" in {
      val request  = FakeRequest(GET, "/clients")
      val response = route(app, request).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
    }
  }

  "ClientController POST " should {

    "add the client and return the added client in subsequent get results" in {

      val time       = Clock.systemUTC().instant()
      val clientName = s"Automated Test client $time"
      val request = FakeRequest(
        POST,
        s"/clients?name=$clientName&email=some.email@server.invalid"
      ).withHeaders("Content-type" -> "application/json")
      val response = route(app, request).get
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")

      val request2 = FakeRequest(GET, "/clients")
        .withHeaders("Content-type" -> "application/json")
      val response2 = route(app, request2).get
      status(response2) mustBe OK
      contentType(response2) mustBe Some("application/json")
      val bodyTextContainsAddedClient =
        contentAsString(response2).contains(clientName)
      bodyTextContainsAddedClient mustEqual true
    }
  }

  "ClientController POST " should {

    "fail if a client with the same e-mail is added more than one time" in {

      val time       = Clock.systemUTC().instant()
      val clientName = s"Automated Test client $time"
      val request = FakeRequest(
        POST,
        s"/clients?name=$clientName&email=duplicate.email@server.invalid"
      ).withHeaders("Content-type" -> "application/json")
      val response = route(app, request).get
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")

      val time2       = Clock.systemUTC().instant()
      val clientName2 = s"Automated Test client $time2"
      val request2 = FakeRequest(
        POST,
        s"/clients?name=$clientName2&email=duplicate.email@server.invalid"
      ).withHeaders("Content-type" -> "application/json")
      val response2 = route(app, request2).get
      status(response2) must not be OK
    }
  }

  "ClientController GET by ID" should {

    "return JSON data " in {
      val request =
        FakeRequest(GET, "/clientsbyid?id=1bb44a7e-cd7c-447d-a9e9-26495b52fa88")
      val response = route(app, request).get

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
    }
  }
}
