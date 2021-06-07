package domain.services

import com.google.inject.ImplementedBy
import domain.models.Project
import persistence.dao.ProjectDAO
import web.dto.{AddProjectDTO, UpdateProjectDTO}

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentProjectRepository])
trait ProjectRepository extends Repository[Project] {
  def byId(i: UUID): Option[Project]
  def all: Seq[Project]
  def add(project: Project): Unit
  def update(project: Project): Unit
  def dtoAsProject(dto: AddProjectDTO): Project
  def dtoAsProject(dto: UpdateProjectDTO): Project
}

class DevelopmentProjectRepository @Inject() (
  projectDao: ProjectDAO,
  clientRepo: ClientRepository,
  userRepo: UserRepository
) extends ProjectRepository {

  def byId(id: UUID): Option[Project] = projectDao.getById(id)

  def dtoAsProject(dto: AddProjectDTO): Project =
    Project(
      name = dto.name,
      description = dto.description,
      client = clientRepo.byId(dto.client).get, // TODO: avoid calling get
      owner = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      createdBy = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      managers = List(userRepo.byId(dto.owner).get), // TODO: avoid calling get
      editedBy = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      billable = dto.billable,
      employees = dto.employees.map(userRepo.byId(_).get), // TODO: avoid calling get
      hourlyCost = dto.hourlyCost
    )

  def dtoAsProject(dto: UpdateProjectDTO): Project =
    Project(
      id = dto.id,
      name = dto.name,
      description = dto.description,
      client = clientRepo.byId(dto.client).get, // TODO: avoid calling get
      owner = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      createdBy = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      managers = List(userRepo.byId(dto.owner).get), // TODO: avoid calling get
      editedBy = userRepo.byId(dto.owner).get, // TODO: avoid calling get
      billable = dto.billable,
      employees = dto.employees.map(userRepo.byId(_).get), // TODO: avoid calling get
      hourlyCost = dto.hourlyCost
    )

  def all: Seq[Project]              = projectDao.getAll
  def add(project: Project): Unit    = projectDao.add(project)
  def update(project: Project): Unit = projectDao.update(project)
}
