package web.controllers

import domain.models.Client
import domain.services.ClientRepository
import io.swagger.annotations._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.UUID.randomUUID
import javax.inject._

@Api
class ClientController @Inject() (
  cc: ControllerComponents,
  clientRepo: ClientRepository
) extends AbstractController(cc)
    with Logging {

  @ApiOperation(value = "Get all clients")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "OK",
        response = classOf[Client],
        responseContainer = "List"
      )
    )
  )
  def listClients: Action[AnyContent] =
    Action {
      try {
        val clients = clientRepo.all
        val json    = Json.toJson(clients)
        Ok(json)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error retrieving clients: $error"}""")
            .as(JSON)
      }
    }

  @ApiOperation(value = "Return a client matching the parameter UUID, if found")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "Returned a client",
        response = classOf[Client]
      ),
      new ApiResponse(code = 400, message = "Error retrieving client")
    )
  )
  def byId(
    @ApiParam(value = "UUID of the client to fetch", required = true) id: String
  ): Action[AnyContent] =
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

  @ApiOperation(value = "Insert new client")
  def add(name: String, email: String): Action[AnyContent] =
    Action {
      try {
        val ZERO: Long = 0;
        val nameDec    = URLDecoder.decode(name, StandardCharsets.UTF_8.toString)
        val emailDec   = URLDecoder.decode(email, StandardCharsets.UTF_8.toString)
        clientRepo.add(Client(randomUUID(), nameDec, emailDec, ZERO, ZERO))
        Ok(s"""{"message": "Successfully inserted a client: $nameDec"}""")
          .as(JSON)
      } catch {
        case error: Exception =>
          logger.error(error.getMessage)
          BadRequest(s"""{"message": "Error inserting a client: $error"}""")
            .as(JSON)
      }
    }
}
