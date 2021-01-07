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
  def inject[T: ClassTag]: T  = injector.instanceOf[T]
}

class TimeInputControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with ScalaFutures {

  // Inject time input repository.
  val timeInputRepository: TimeInputRepository =
    TimeInputInject.inject[TimeInputRepository]
  implicit lazy val executionContext = TimeInputInject.inject[ExecutionContext]
  val applicationJson                = "application/json"
  val hoursUrl                       = "/hours"

  "TimeInputController GET" should {
    "return JSON data" in {
      val timeInputFetch = FakeRequest(GET, hoursUrl)
      val fetchResponse  = route(app, timeInputFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)
    }
  }

  "TimeInputController POST" should {
    "parse TimeInput JSON correctly" in {
      val validTimeInput =
        """{"input": 450,
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2020-12-17"}""".stripMargin
      val timeInputJson = Json.parse(validTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe OK
      contentType(createResponse) mustBe Some(applicationJson)
    }

    "reject empty TimeInput JSON" in {
      val emptyString = "{}"
      val emptyJson   = Json.parse(emptyString)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](emptyJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe BAD_REQUEST
    }

    "result in a TimeInput being recorded and retrievable" in {
      val validTimeInput =
        """{"input": 450,
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2000-12-17"}""".stripMargin
      val timeInputJson = Json.parse(validTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe OK
      contentType(createResponse) mustBe Some(applicationJson)

      val timeInputFetch = FakeRequest(GET, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val fetchResponse = route(app, timeInputFetch).get

      val bodyTextContainsPostedTime =
        contentAsString(fetchResponse).contains("2000-12-17")

      bodyTextContainsPostedTime mustEqual true
    }

    "reject negative time input" in {
      val negativeTimeInput =
        """{"input": -450,
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2020-12-17"}""".stripMargin
      val timeInputJson = Json.parse(negativeTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe BAD_REQUEST
    }

    "reject decimal time input" in {
      val decimalTimeInput =
        """{"input": 4.5,
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2020-12-17"}""".stripMargin
      val timeInputJson = Json.parse(decimalTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe BAD_REQUEST
    }

    "reject duplicate input" in {
      val validTimeInput =
        """{"input": 450,
          |"project": "44e4653d-7f71-4cf2-90f3-804f949ba264",
          |"employee": "a3f4e844-4199-439d-a463-2f07e87c6ca4",
          |"date": "2020-12-17"}""".stripMargin
      val timeInputJson = Json.parse(validTimeInput)

      val firstTimeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)
      val duplicateTimeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val firstCreateResponse     = route(app, firstTimeInputCreate).get
      val duplicateCreateResponse = route(app, duplicateTimeInputCreate).get
      val bothResponses =
        Vector(status(firstCreateResponse), status(duplicateCreateResponse))
      bothResponses.contains(CONFLICT) mustEqual true
      bothResponses.contains(OK) mustEqual true
    }

  }
}
