package web.controllers

import domain.models.{ConflictException, InvalidDataException}
import domain.services.ClientRepository
import io.swagger.annotations._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import web.dto.{AddClientDTO, ClientDTO}

import java.util.{NoSuchElementException, UUID}
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
        response = classOf[ClientDTO],
        responseContainer = "List"
      )
    )
  )
  def listClients: Action[AnyContent] =
    Action {
      try { Ok(Json.toJson(clientRepo.all.map(ClientDTO.fromDomain))) }
      catch {
        case t: Throwable =>
          logger.error(t.getMessage, t)
          InternalServerError
      }
    }

  @ApiOperation(value = "Return a client matching the parameter UUID, if found")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "Returned a client",
        response = classOf[ClientDTO]
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
        Ok(
          Json.toJson(
            ClientDTO.fromDomain(clientRepo.byId(UUID.fromString(id)).get)
          )
        )
      } catch {
        case _: IllegalArgumentException => BadRequest
        case _: NoSuchElementException   => NotFound
        case t: Throwable =>
          logger.error(t.getMessage, t)
          InternalServerError
      }
    }

  @ApiOperation(value = "Insert new client")
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 201,
        message = "Inserted new client",
        response = classOf[ClientDTO]
      ),
      new ApiResponse(
        code = 200,
        message = "OK",
        response = classOf[ClientDTO]
      ),
      new ApiResponse(code = 400, message = "Bad request"),
      new ApiResponse(code = 409, message = "Email already in use"),
      new ApiResponse(code = 422, message = "Client name or email empty")
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
        val client    = AddClientDTO.toDomain(clientDTO)
        clientRepo.add(client)
        Created(Json.toJson(ClientDTO.fromDomain(client)))
      } catch {
        case error: InvalidDataException =>
          logger.error(error.getMessage, error)
          UnprocessableEntity(error.getMessage)
        case conflict: ConflictException =>
          logger.error(conflict.getMessage, conflict)
          Conflict(conflict.getMessage)
        case t: Throwable =>
          logger.error(t.getMessage, t)
          InternalServerError
      }
    }
}
