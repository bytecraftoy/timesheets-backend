package domain.services

import com.google.inject.ImplementedBy
import domain.models.User
import persistence.dao.UserDAO
import play.api.Logging

import java.util.UUID
import javax.inject.Inject

@ImplementedBy(classOf[DevelopmentUserRepository])
trait UserRepository extends Repository[User] with Logging {
  def byId(userId: UUID): User
  def all: Seq[User]
  def add(user: User): Unit
  def addUserToProject(userId: UUID, projectId: UUID): Unit
  def removeUserFromProject(userId: UUID, projectId: UUID): Unit
  def getAllManagers(): Seq[User]
  def getManagersByProjectId(projectId: UUID): Seq[User]
  def getEmployeesByProjectId(projectId: UUID): Seq[User]
}

class DevelopmentUserRepository @Inject() (userDao: UserDAO)
    extends UserRepository {

  def getAllManagers(): Seq[User] = userDao.getAllManagers()
  def getManagersByProjectId(projectId: UUID): Seq[User] =
    userDao.getManagersByProjectId(projectId)
  def getEmployeesByProjectId(projectId: UUID): Seq[User] =
    userDao.getEmployeesByProjectId(projectId)
  def byId(userId: UUID): User = userDao.getById(userId)
  def all: Seq[User]           = userDao.getAll()
  def add(user: User): Unit    = ???
  def addUserToProject(userId: UUID, projectId: UUID): Unit =
    userDao.addUserToProject(userId, projectId)
  def removeUserFromProject(userId: UUID, projectId: UUID): Unit =
    userDao.removeUserFromProject(userId, projectId)
}
