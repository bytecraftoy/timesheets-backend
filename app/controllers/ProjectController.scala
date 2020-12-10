package controllers

import play.api.libs.json.{JsObject, Json}
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

  def addProject: Action[AnyContent] =
    Action { request =>
      val json =
        Json
          .using[Json.WithDefaultValues]
          .parse(request.body.asJson.get.toString)
      val emp =
        models.User(
          json.as[JsObject].value("owner").toString.filterNot(_ == '"').toInt
        ) // TODO: handle lack of id
      val updatedJson =
        json.as[JsObject] ++ Json.obj("owner" -> emp)
      val project = updatedJson.as[Project]
      Project.add(project)
      Ok(updatedJson)
    }
}
