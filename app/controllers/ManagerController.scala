package controllers

import play.api.libs.json.Json
import javax.inject._
import play.api.mvc._
import models.User

class ManagerController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def listEmployees: Action[AnyContent] =
    Action {
      val json = Json.toJson(User.all)
      Ok(json)
    }
}
