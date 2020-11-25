package controllers

import play.api.libs.json.Json
import javax.inject._
import play.api.mvc._
import models.Project

// https://www.playframework.com/documentation/2.8.x/ScalaJsonHttp

class ProjectController @Inject() (cc: ControllerComponents)
    extends AbstractController(cc) {

  def listProjects: Action[AnyContent] =
    Action {
      val json = Json.toJson(Project.all)
      Ok(json)
    }
}
