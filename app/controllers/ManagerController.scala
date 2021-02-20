package controllers

import io.swagger.annotations.Api
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._
import models.UserRepository
import play.api.Logging

@Api
class ManagerController @Inject() (
  cc: ControllerComponents,
  userRepo: UserRepository
) extends AbstractController(cc)
    with Logging {

  def listManagers: Action[AnyContent] =
    Action {
      try {
        val managers = userRepo.getAllManagers()
        val json     = Json.toJson(managers)
        Ok(json)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error retrieving managers: $error"}""")
            .as(JSON)

      }
    }
}
