package models

import play.api.libs.json.{Json, OWrites, Reads}
import scala.collection.mutable.ArrayBuffer

case class Client(
  id: BigInt = Client.all.maxBy(_.id).id + 1,
  name: String = "client " + (User.all.maxBy(_.id).id + 1)
)

object Client {
  val client1: Client =
    Client(id = 1, name = "Client 1")
  val client2: Client =
    Client(id = 2, name = "Client 2")
  val client3: Client =
    Client(id = 3, name = "Client 3")
  val client4: Client =
    Client(id = 4, name = "Client 4")

  implicit val readClient: Reads[Client] = Json.reads[Client]

  implicit val writeClient: OWrites[Client] = Json.writes[Client]

  val all: ArrayBuffer[Client] =
    ArrayBuffer(client1, client2, client3, client4)

  def add(client: Client): Unit = all append client
}
