package controllers

import play.api.libs.json.{Json, Reads}

import javax.inject._
import play.api.mvc._
import models.{Client, ClientRepository, Project, User}
import play.api.Logging

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.{Calendar, UUID}
import java.util.UUID.randomUUID

class ClientController @Inject() (
  cc: ControllerComponents,
  clientRepo: ClientRepository
) extends AbstractController(cc)
    with Logging {

  def listClients: Action[AnyContent] =
    Action {
      try {
        val clients = clientRepo.all
        val json    = Json.toJson(clients)
        Ok(json)
      } catch {
        case error: Exception =>
          BadRequest(s"""{"message": "Error retrieving clients: $error"}""")
            .as(JSON)
      }
    }

  def byId(id: String): Action[AnyContent] =
    Action {
      try {
        val uuid   = UUID.fromString(id)
        val client = clientRepo.byId(uuid)

        if (client != null) {
          val json = Json.toJson(client)
          Ok(json)
        } else {
          BadRequest(
            s"""{"message": "Error retrieving a client with client id = $id"}"""
          ).as(JSON)
        }

      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error retrieving a client: $error"}""")
            .as(JSON)
      }
    }

  def add(name: String, email: String): Action[AnyContent] =
    Action {
      try {
        val ZERO: Long = 0;
        val nameDec  = URLDecoder.decode(name, StandardCharsets.UTF_8.toString)
        val emailDec = URLDecoder.decode(email, StandardCharsets.UTF_8.toString)
        clientRepo.add(
          Client(
            randomUUID(),
            nameDec,
            emailDec,
            ZERO,
            ZERO
          )
        )
        Ok(s"""{"message": "Successfully inserted a client: $nameDec"}""")
          .as(JSON)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error inserting a client: $error"}""")
            .as(JSON)
      }
    }

  case class AddProjectDTO(
    name: String,
    description: String,
    client: UUID,
    owner: UUID,
    billable: Boolean
  ) {

    def asProject: Project =
      Project(
        name = this.name,
        description = this.description,
        client = clientRepo.byId(this.client),
        owner = User.byId(this.owner),
        creator = User.byId(this.owner),
        managers = List(User.byId(this.owner)),
        lastEditor = User.byId(this.owner),
        billable = this.billable
      )
  }
  object AddProjectDTO {
    implicit val readProjectDTO: Reads[AddProjectDTO] =
      Json.reads[AddProjectDTO]
  }
}
