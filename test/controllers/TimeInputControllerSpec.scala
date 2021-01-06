package controllers

import models.TimeInputRepository
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue


import scala.concurrent.{Await, ExecutionContext}
import scala.reflect.ClassTag

object TimeInputInject {
  lazy val injector: Injector = (new GuiceApplicationBuilder).injector()
  def inject[T: ClassTag]: T = injector.instanceOf[T]
}

class TimeInputControllerSpec
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with ScalaFutures {

  // Inject time input repository.
  val timeInputRepository: TimeInputRepository = TimeInputInject.inject[TimeInputRepository]
  implicit lazy val executionContext = TimeInputInject.inject[ExecutionContext]
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
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
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
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2000-12-17"}""".stripMargin
      val json = Json.parse(jsonString)

      val request = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](json)

      val result = route(app, request).get
      status(result) mustBe OK
      contentType(result) mustBe Some(applicationJson)

      val requestGet = FakeRequest(GET, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](json)

      val resultGet = route(app, requestGet).get

      val bodyTextContainsPostedTime = contentAsString(resultGet).contains("2000-12-17")

      bodyTextContainsPostedTime mustEqual true
      }


    }
}
