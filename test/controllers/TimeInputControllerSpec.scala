package controllers

import models.TimeInputRepository
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Logging
import play.api.Play.materializer
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.json.JsValue

import java.time.Clock
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
    with ScalaFutures
    with Logging {

  // Inject time input repository.
  val timeInputRepository: TimeInputRepository =
    TimeInputInject.inject[TimeInputRepository]
  implicit lazy val executionContext = TimeInputInject.inject[ExecutionContext]
  val applicationJson                = "application/json"
  val testProject                    = "a3eb6db5-5212-46d0-bd08-8e852a45e0d3"
  val testUser                       = "9fa407f4-7375-446b-92c6-c578839b7780"
  val hoursUrl                       = "/hours"
  val hoursUrlGetDateIntervalForProject =
    s"/projects/$testProject/hours?userId=$testUser&startDate=2000-01-01&endDate=2030-12-31"

  val hoursUrlGetWithProject = s"/projects/$testProject/"
  val sqlInjection1          = """hours?userId=' OR 1=1;--"""
  val sqlInjection2          = """hours?userId=" OR 1=1;--"""
  val sqlInjection3          = """hours?userId=$testUser&startDate=' OR 1=1;--"""

  "TimeInputController GET" should {
    "return JSON data" in {
      val timeInputFetch = FakeRequest(GET, hoursUrlGetDateIntervalForProject)
      val fetchResponse  = route(app, timeInputFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)
    }

    "reject SQL injection GET attempts" in {
      val timeInputFetch =
        FakeRequest(GET, hoursUrlGetWithProject + sqlInjection1)
      val fetchResponse = route(app, timeInputFetch).get

      status(fetchResponse) mustBe BAD_REQUEST
      contentType(fetchResponse) mustBe Some(applicationJson)

      val timeInputFetch2 =
        FakeRequest(GET, hoursUrlGetWithProject + sqlInjection2)
      val fetchResponse2 = route(app, timeInputFetch2).get

      status(fetchResponse2) mustBe BAD_REQUEST
      contentType(fetchResponse2) mustBe Some(applicationJson)

      val timeInputFetch3 =
        FakeRequest(GET, hoursUrlGetWithProject + sqlInjection2)
      val fetchResponse3 = route(app, timeInputFetch3).get

      status(fetchResponse3) mustBe BAD_REQUEST
      contentType(fetchResponse3) mustBe Some(applicationJson)
    }
  }

  "TimeInputController POST" should {
    "parse TimeInput JSON correctly" in {
      val validTimeInput =
        s"""{"input": 450,
          |"project": "$testProject",
          |"employee": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"date": "2000-12-01",
          |"description": "testikuvaus"}""".stripMargin
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
      val uniqueTestInputDate = "2000-12-02"
      val testDescription     = s"""This is a test $uniqueTestInputDate"""
      val validTimeInput =
        s"""{"input": 450,
           |"project": "$testProject",
           |"employee": "9fa407f4-7375-446b-92c6-c578839b7780",
           |"date": "$uniqueTestInputDate",
           |"description":"$testDescription"}""".stripMargin
      val timeInputJson = Json.parse(validTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe OK
      contentType(createResponse) mustBe Some(applicationJson)

      val timeInputFetch = FakeRequest(GET, hoursUrlGetDateIntervalForProject)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val fetchResponse = route(app, timeInputFetch).get

      val bodyTextContainsPostedTime =
        contentAsString(fetchResponse).contains(uniqueTestInputDate)

      val bodyTextContainsPostedDescription =
        contentAsString(fetchResponse).contains(testDescription)

      bodyTextContainsPostedTime mustEqual true
      bodyTextContainsPostedDescription mustEqual true
    }

    "reject negative time input" in {
      val negativeTimeInput =
        s"""{"input": -450,
          |"project": "$testProject",
          |"employee": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"date": "2000-12-03",
          |"description": "testikuvaus"}""".stripMargin
      val timeInputJson = Json.parse(negativeTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe BAD_REQUEST
    }

    "reject decimal time input" in {
      val decimalTimeInput =
        s"""{"input": 4.5,
          |"project": "$testProject",
          |"employee": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"date": "2000-12-04",
          |"description": "testikuvaus"}""".stripMargin
      val timeInputJson = Json.parse(decimalTimeInput)

      val timeInputCreate = FakeRequest(POST, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val createResponse = route(app, timeInputCreate).get
      status(createResponse) mustBe BAD_REQUEST
    }

    "reject duplicate input" in {
      val validTimeInput =
        s"""{"input": 450,
          |"project": "$testProject",
          |"employee": "9fa407f4-7375-446b-92c6-c578839b7780",
          |"date": "2000-12-05",
          |"description": "testikuvaus"}""".stripMargin
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
      bothResponses.contains(BAD_REQUEST) mustEqual true
      bothResponses.contains(OK) mustEqual true
    }
  }

  "TimeInputController PUT" should {
    "result in an updated TimeInput" in {

      // the row has been inserted in the test data creation
      val newTimeInput = 999
      val newTestDescription =
        s"""This is an update test ${Clock.systemDefaultZone().instant()}"""
      val timeInputToUpdate =
        s"""{"id": "65205173-2019-41e2-bacc-88bbd913d5a7",
             |"input": $newTimeInput,
             |"description": "$newTestDescription"}""".stripMargin
      logger.debug(s"TimeInput update test JSON: $timeInputToUpdate")
      val timeInputJson = Json.parse(timeInputToUpdate)

      val timeInputUpdate = FakeRequest(PUT, hoursUrl)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val updateResponse = route(app, timeInputUpdate).get
      status(updateResponse) mustBe OK
      contentType(updateResponse) mustBe Some(applicationJson)

      val timeInputFetch = FakeRequest(GET, hoursUrlGetDateIntervalForProject)
        .withHeaders("Content-type" -> applicationJson)
        .withBody[JsValue](timeInputJson)

      val fetchResponse       = route(app, timeInputFetch).get
      val fetchResponseString = contentAsString(fetchResponse)

      logger.debug(s"TimeInput update test GET response: $fetchResponseString")

      val bodyTextContainsNewDescription =
        fetchResponseString.contains(newTestDescription)
      val bodyTextContainsNewTimeInput =
        fetchResponseString.contains(newTimeInput.toString)

      bodyTextContainsNewDescription mustEqual true
      bodyTextContainsNewTimeInput mustEqual true
    }
  }
}
