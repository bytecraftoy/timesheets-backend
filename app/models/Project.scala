package models

import play.api.libs.json.{JsValue, Json, OFormat, OWrites, Reads, _}

import java.util.UUID.randomUUID
import java.util.UUID
import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer

case class Project (
                     id: UUID = randomUUID(),
                     name: String = "",
                     description: String = "",
                     owner: User = User.dummyManager,
                     creator: User = User.dummyManager,
                     managers: List[User] = List(),
                     client: Client = Client.client1,
                     billable: Boolean = true,
                     employees: List[User] = List(),
                     tags: List[String] = List(),
                     creationTimestamp: Long = System.currentTimeMillis(),
                     lastEdited: Long = System.currentTimeMillis(),
                     lastEditor: User = User.dummyManager2

                  ) {
  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]
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
      client = Client.byId(this.client),
      owner = User.byId(this.owner),
      creator = User.byId(this.owner),
      managers = List(User.byId(this.owner)),
      lastEditor = User.byId(this.owner),
      billable = this.billable
    )
}
object AddProjectDTO {
  implicit val readProjectDTO: Reads[AddProjectDTO] = Json.reads[AddProjectDTO]
}
