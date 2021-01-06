package models

import play.api.libs.json.{Json, OWrites, Reads}
import scala.collection.mutable.ArrayBuffer
import java.util.UUID.randomUUID
import java.util.UUID

case class Client(
  id: UUID = randomUUID(),
  name: String = "client " + System.currentTimeMillis()
)

object Client {
  val client1: Client =
    Client(id = UUID.fromString("aecce2fd-753e-4bb9-b9e4-4ff57a04d6b5"), name = "Client 1")
  val client2: Client =
    Client(id = UUID.fromString("ad6e67b5-01ec-404d-817e-462fc2288b39"), name = "Client 2")
  val client3: Client =
    Client(id = UUID.fromString("05c27152-b4ac-4b70-9b4b-6755e40be776"), name = "Client 3")
  val client4: Client =
    Client(id = UUID.fromString("137038ad-400a-4971-b4bd-c595b461de72"), name = "Client 4")
  val dbClient1: Client =
    Client(id = UUID.fromString("1bb44a7e-cd7c-447d-a9e9-26495b52fa88"), name = "Esimerkkiasiakas")

  implicit val readClient: Reads[Client] = Json.reads[Client]

  implicit val writeClient: OWrites[Client] = Json.writes[Client]

  def byId(i: UUID): Client = Client.all.filter(_.id == i).head

  val all: ArrayBuffer[Client] =
    ArrayBuffer(client1, client2, client3, client4, dbClient1)

  def add(client: Client): Unit = all append client
}
