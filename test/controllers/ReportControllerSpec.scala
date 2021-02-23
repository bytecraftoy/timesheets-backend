package controllers

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import models.{ClientReportService, SalaryReportService}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Logging
import play.api.http.Status.OK
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Action, AnyContent}
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers.{GET, contentType, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty}

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
  val testEmployeeUUIDString: String = "9fa407f4-7375-446b-92c6-c578839b7780"
  val testClientUUIDString: String  = "1bb44a7e-cd7c-447d-a9e9-26495b52fa88"
  val startDateString: String       = "2020-01-01"
  val endDateString: String         = "2021-04-30"

  val clientReportPath = "/report/client/"
  val clientReportFull =
    s"""/report/client/$testClientUUIDString?projects=$testProjectUUIDString&startDate=$startDateString&endDate=$endDateString&employees=$testEmployeeUUIDString"""

  val salaryReportPath = "/report/employee/"
  val salaryReportFull = s"""/report/employee/$testEmployeeUUIDString?startDate=$startDateString&endDate=$endDateString&clients=$testClientUUIDString"""

  "ReportController getClientReport" should {
    "return an Action" in {
      val getClientReportReturn = reportController.getClientReport(
        clientIdString = testClientUUIDString,
        projectIdStringList = List(testProjectUUIDString),
        employeeIdStringList = List(testEmployeeUUIDString),
        startDateString = startDateString,
        endDateString = endDateString
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
  }


  "ReportController getSalaryReport" should {
    "return an Action" in {
      val getSalaryReportReturn = reportController.getSalaryReport(
        employeeIdString = testEmployeeUUIDString,
        clientIdStrings = List(testClientUUIDString),
        startDateString = startDateString,
        endDateString = endDateString
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
  }

}
