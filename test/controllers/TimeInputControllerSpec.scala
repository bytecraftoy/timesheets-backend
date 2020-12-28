package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

class TimeInputControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "TimeInputController GET" should {
    "return JSON data" in {
      val request = FakeRequest(GET, "/hours")
      val result  = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }
  }

  "TimeInputController POST" should {
    "parse TimeInput JSON correctly" in {
      val jsonString =
        """{"input": 7.5,
          |"project": 1000,
          |"employee": 1,
          |"date": "2020-12-17"}""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, "/hours")
        .withHeaders("Content-type" -> "application/json")
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
    }
  }
}
