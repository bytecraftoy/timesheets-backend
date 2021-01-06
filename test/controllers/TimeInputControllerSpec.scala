package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import java.time.LocalDate

class TimeInputControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  val applicationJson = "application/json"
  val hoursUrl        = "/hours"

  "TimeInputController GET" should {
    "return JSON data" in {
      val request = FakeRequest(GET, hoursUrl)
      val result  = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some(applicationJson)
    }
  }

  "TimeInputController POST" should {
    "parse TimeInput JSON correctly" in {
      val jsonString =
        """{"input": 450,
          |"project": 1000,
          |"employee": 1,
          |"date": "2020-12-17"}""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some(applicationJson)
    }

    "reject invalid TimeInput JSON" in {
      val jsonString = "{}"
      val json       = Json.parse(jsonString)

      val request = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe BAD_REQUEST
    }

    "result in a TimeInput being recorded and retrievable" in {
      val jsonString =
        """{"input": 450,
          |"project": 1000,
          |"employee": 1,
          |"date": "2000-12-17"}""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some(applicationJson)
      models.TimeInput.all
        .exists(_.date.equals(LocalDate.of(2000, 12, 17))) mustEqual (true)

    }
  }
}
