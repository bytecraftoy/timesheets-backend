package web.controllers

import domain.models.Client
import domain.services.ClientRepository
import io.swagger.annotations._
import org.h2.jdbc.JdbcSQLException
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import web.dto.{AddClientDTO, ClientMapper}

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.{NoSuchElementException, UUID}
import javax.inject._

@Api
class ClientController @Inject() (
  cc: ControllerComponents,
  clientRepo: ClientRepository,
  clientMapper: ClientMapper
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
      try { Ok(Json.toJson(clientRepo.all)) }
      catch {
        case unhandled: Exception =>
          logger.error(unhandled.getMessage)
          InternalServerError
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
      new ApiResponse(code = 400, message = "Error retrieving client"),
      new ApiResponse(code = 404, message = "Client not found")
    )
  )
  def byId(
    @ApiParam(value = "UUID of the client to fetch", required = true) id: String
  ): Action[AnyContent] =
    Action {
      try {
        Ok(Json.toJson(clientRepo.byId(UUID.fromString(id)).get))
      } catch {
        case _: IllegalArgumentException => BadRequest
        case _: NoSuchElementException   => NotFound
        case unhandled: Exception =>
          logger.error(unhandled.getMessage)
          InternalServerError
      }
    }

  @ApiOperation(value = "Insert new client")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 201,
        message = "Inserted new client",
        response = classOf[Client]
      ),
      new ApiResponse(code = 200, message = "OK", response = classOf[Client]),
      new ApiResponse(code = 400, message = "Bad request"),
      new ApiResponse(code = 409, message = "Email already in use")
    )
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "Client to add",
        paramType = "body",
        dataType = "web.dto.AddClientDTO"
      )
    )
  )
  def add(): Action[AddClientDTO] =
    Action(parse.json[AddClientDTO]) { request =>
      try {
        val clientDTO = request.body
        val client    = clientMapper.dtoAsClient(clientDTO)
        clientRepo.add(client)
        Created(Json.toJson(client))
      } catch {
        case error: IllegalArgumentException => BadRequest(error.getMessage)
        case _: JdbcSQLException         => Conflict("Email already in use")
      }
    }
}
