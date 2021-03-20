package models

import com.google.inject.ImplementedBy
import dao.ProjectDAO
import play.api.libs.json.{Json, OFormat}

import java.time.Clock
import java.util.UUID.randomUUID
import java.util.{Calendar, UUID}
import javax.inject.Inject
import scala.collection.mutable.ArrayBuffer

@ImplementedBy(classOf[DevelopmentProjectRepository])
trait ProjectRepository extends Repository[Project] {
  def byId(i: UUID): Project
  def all: Seq[Project]
  def add(project: Project): Unit
  def update(project: Project): Unit
}

class DevelopmentProjectRepository @Inject() (projectDao: ProjectDAO)
    extends ProjectRepository {

  val dummy: Project =
    Project(
      id = UUID.fromString("44e4653d-7f71-4cf2-90f3-804f949ba264"),
      name = "Dummy",
      description = "This is a dummy project.",
      owner = User.dummyManager,
      creator = User.dummyManager,
      managers = List(User.dummyManager, User.dummyManager2),
      client = Client(
        randomUUID(),
        "client " + Clock.systemUTC().instant(),
        "some@email.invalid",
        Calendar.getInstance().getTimeInMillis,
        Calendar.getInstance().getTimeInMillis
      ),
      billable = false,
      employees = List(User.dummyEmployee, User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager
    )

  val dummy2: Project =
    Project(
      id = UUID.fromString("6ffdeea7-c6bd-42e5-a5a3-49526cbd001a"),
      name = "Another dummy",
      description = "This is another dummy project.",
      owner = User.dummyManager2,
      creator = User.dummyManager2,
      managers = List(User.dummyManager2),
      client = Client(
        randomUUID(),
        "client " + Clock.systemUTC().instant(),
        "some@email.invalid",
        Calendar.getInstance().getTimeInMillis,
        Calendar.getInstance().getTimeInMillis
      ),
      billable = false,
      employees = List(User.dummyEmployee2),
      tags = List("Back-end", "Front-end", "Fullstack", "Planning"),
      creationTimestamp = 100000000000L,
      lastEdited = 100000000010L,
      lastEditor = User.dummyManager2
    )

  def byId(id: UUID): Project = projectDao.getById(id)

  implicit def projectFormat: OFormat[Project] =
    Json.using[Json.WithDefaultValues].format[Project]

  //val projectsInMemory = ArrayBuffer(dummy, dummy2)
  def all: Seq[Project]              = projectDao.getAll() //++projectsInMemory.toSeq
  def add(project: Project): Unit    = projectDao.add(project)
  def update(project: Project): Unit = projectDao.update(project)
}
