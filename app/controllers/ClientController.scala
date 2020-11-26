package controllers

import play.api.libs.json.Json
import javax.inject._
import play.api.mvc._
import models.Client

class ClientController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def listClients: Action[AnyContent] =
    Action {
      val json = Json.toJson(Client.all)
      Ok(json)
    }
}
