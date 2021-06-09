package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{JsValue, Json}
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

    def jsonStringForPostBody(
      clientName: String = "Some Client",
      clientEmail: String = "some@client.com"
    ): String = s"""{"name": "$clientName","email": "$clientEmail"}"""

    "add the client and return the added client in subsequent get results" in {

      val time       = Clock.systemUTC().instant()
      val clientName = s"Automated Test client $time"
      val json       = Json.parse(jsonStringForPostBody(clientName))
      val request = FakeRequest(POST, "/clients")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)
      val response = route(app, request).get
      status(response) mustBe CREATED
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

    "fail if a client with the same e-mail is added more than one time" in {

      val duplicateEmail = "duplicate@client.com"
      val time           = Clock.systemUTC().instant()
      val clientName1    = s"Automated Test client $time"
      val json = Json.parse(
        jsonStringForPostBody(
          clientName = clientName1,
          clientEmail = duplicateEmail
        )
      )
      val request = FakeRequest(POST, "/clients")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)
      val response = route(app, request).get
      status(response) mustBe CREATED
      contentType(response) mustBe Some("application/json")

      val time2       = Clock.systemUTC().instant()
      val clientName2 = s"Automated Test client $time2"
      val json2 = Json.parse(
        jsonStringForPostBody(
          clientName = clientName2,
          clientEmail = duplicateEmail
        )
      )
      val request2 = FakeRequest(POST, "/clients")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json2)
      val response2 = route(app, request2).get
      status(response2) mustBe CONFLICT
    }

    "fail if name or email is empty" in {
      val json = Json.parse(jsonStringForPostBody("", ""))
      val request = FakeRequest(POST, "/clients")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)
      val response = route(app, request).get
      status(response) mustBe BAD_REQUEST
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
