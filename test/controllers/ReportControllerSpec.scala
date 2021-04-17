package controllers

import domain.services.{ClientReportService, SalaryReportService}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Logging
import play.api.http.Status.OK
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}
import play.api.test.Helpers.{
  GET,
  contentAsString,
  contentType,
  defaultAwaitTimeout,
  route,
  status,
  writeableOf_AnyContentAsEmpty
}
import play.api.test.{FakeRequest, Injecting}
import web.controllers.ReportController

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

object ReportInject {
  lazy val injector: Injector = (new GuiceApplicationBuilder).injector()
  def inject[T: ClassTag]: T  = injector.instanceOf[T]
}

class ReportControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with ScalaFutures
    with Logging {

  val clientReportService: ClientReportService =
    ReportInject.inject[ClientReportService]

  val reportController: ReportController =
    ReportInject.inject[ReportController]

  val salaryReportService: SalaryReportService =
    ReportInject.inject[SalaryReportService]

  implicit lazy val executionContext = ReportInject.inject[ExecutionContext]

  val applicationJson = "application/json"

  val testProjectUUIDString: String = "a3eb6db5-5212-46d0-bd08-8e852a45e0d3"
  val allTestProjectUUIDString: String =
    """a3eb6db5-5212-46d0-bd08-8e852a45e0d3&projects=a1eda1a6-a749-4932-9f48-16fe5b6a8ce9&projects=d7e738a2-60cf-4336-b7d9-5216ef960e3a&projects=40726707-57d1-47e0-a82b-6d85320b160b"""
  val nonBillableTestEmployeeUUIDString: String =
    "4276164d-d8c3-47d5-8f65-a6255ce71567"
  val testEmployeeUUIDString: String = "9fa407f4-7375-446b-92c6-c578839b7780"
  val testClientUUIDString: String   = "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
  val testNonBillableClientUUIDString: String =
    "5be59e8a-63f5-4f22-8b12-c7128fb40add"
  val startDateString: String = "2020-01-01"
  val endDateString: String   = "2021-04-30"

  val clientReportPath = "/report/client/"
  val clientReportFull =
    s"""/report/client/$testClientUUIDString?projects=$testProjectUUIDString&startDate=$startDateString&endDate=$endDateString&employees=$testEmployeeUUIDString"""
  val clientReportAllProjects =
    s"""/report/client/$testClientUUIDString?projects=$allTestProjectUUIDString&startDate=$startDateString&endDate=$endDateString&employees=$nonBillableTestEmployeeUUIDString"""

  val salaryReportPath = "/report/employee/"
  val salaryReportFull =
    s"""/report/employee/$testEmployeeUUIDString?startDate=$startDateString&endDate=$endDateString&clients=$testClientUUIDString"""
  val salaryReportAllProjects =
    s"""/report/employee/$nonBillableTestEmployeeUUIDString?startDate=$startDateString&endDate=$endDateString&clients=$testNonBillableClientUUIDString"""

  "ReportController getClientReport" should {
    "return an Action" in {
      val getClientReportReturn = reportController.getClientReport(
        clientIdString = testClientUUIDString,
        projectIdStringList = List(testProjectUUIDString),
        employeeIdStringList = List(testEmployeeUUIDString),
        startDateString = startDateString,
        endDateString = endDateString,
        billable = true,
        nonBillable = true
      )

      getClientReportReturn.isInstanceOf[Action[AnyContent]] mustBe true
    }

    "return data in JSON" in {
      logger.debug(
        s"""ReportControllerSpec --> return JSON data: URL: $clientReportFull"""
      )

      val reportFetch   = FakeRequest(GET, clientReportFull)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)
    }

    "return only billable projects" in {
      val clientReportUrl =
        clientReportAllProjects + "&billable=true&nonBillable=false"

      logger.debug(
        s"""ReportControllerSpec --> return only billable projects: URL: $clientReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, clientReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables(1).as[Boolean] mustBe true
      billables.distinct.length mustBe 1
    }

    "return only non-billable projects" in {
      val clientReportUrl =
        clientReportAllProjects + "&billable=false&nonBillable=true"

      logger.debug(
        s"""ReportControllerSpec --> return only non-billable projects: URL: $clientReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, clientReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables(1).as[Boolean] mustBe false
      billables.distinct.length mustBe 1
    }

    "return both billable and non-billable projects" in {
      val clientReportUrl =
        clientReportAllProjects + "&billable=true&nonBillable=true"

      logger.debug(
        s"""ReportControllerSpec --> return both billable and non-billable projects: URL: $clientReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, clientReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables.exists(v => v.as[Boolean] == true) mustBe true
      billables.exists(v => v.as[Boolean] == false) mustBe true
    }
  }

  "ReportController getSalaryReport" should {
    "return an Action" in {
      val getSalaryReportReturn = reportController.getSalaryReport(
        employeeIdString = testEmployeeUUIDString,
        clientIdStrings = List(testClientUUIDString),
        startDateString = startDateString,
        endDateString = endDateString,
        billable = true,
        nonBillable = true
      )

      getSalaryReportReturn.isInstanceOf[Action[AnyContent]] mustBe true
    }

    "return data in JSON" in {
      logger.debug(
        s"""ReportControllerSpec --> return JSON data: URL: $salaryReportFull"""
      )

      val reportFetch   = FakeRequest(GET, salaryReportFull)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

    }

    "return only billable projects" in {
      val salaryReportUrl =
        salaryReportAllProjects + "&billable=true&nonBillable=false"

      logger.debug(
        s"""ReportControllerSpec --> return only billable projects: URL: $salaryReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, salaryReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables(1).as[Boolean] mustBe true
      billables.distinct.length mustBe 1
    }

    "return only non-billable projects" in {
      val salaryReportUrl =
        salaryReportAllProjects + "&billable=false&nonBillable=true"

      logger.debug(
        s"""ReportControllerSpec --> return only non-billable projects: URL: $salaryReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, salaryReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables(1).as[Boolean] mustBe false
      billables.distinct.length mustBe 1
    }

    "return both billable and non-billable projects" in {
      val salaryReportUrl =
        salaryReportAllProjects + "&billable=true&nonBillable=true"

      logger.debug(
        s"""ReportControllerSpec --> return both billable and non-billable projects: URL: $salaryReportUrl"""
      )

      val reportFetch   = FakeRequest(GET, salaryReportUrl)
      val fetchResponse = route(app, reportFetch).get

      status(fetchResponse) mustBe OK
      contentType(fetchResponse) mustBe Some(applicationJson)

      val jsonStr   = contentAsString(fetchResponse)
      val json      = Json.parse(jsonStr)
      val billables = json \\ "billable"

      billables.length must be > 0
      billables.exists(v => v.as[Boolean] == true) mustBe true
      billables.exists(v => v.as[Boolean] == false) mustBe true
    }
  }
}
