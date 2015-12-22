package pt.org.apec.services.users.dal

import scala.concurrent.ExecutionContext
import slick.driver.JdbcDriver
import godiva.slick._
import org.mindrot.jbcrypt.BCrypt
import org.joda.time.DateTime
import pt.org.apec.services.users.common._
import scala.concurrent.Future
import org.slf4j.LoggerFactory

trait Dal[DT <: JdbcDriver] extends JodaSupport[DT] with UsersComponent[DT] with AuthorizationComponent[DT] with TablesSchema {
  this: DriverComponent[DT] with DatabaseComponent[DT] with DefaultExecutionContext with SchemaManagement =>
  import driver.api._
  import Joda._

  def config: DalConfig
  val logger = LoggerFactory.getLogger(getClass)

  // user related methods
  // make email verification working.
  def createUser(username: String, password: String, primaryEmail: String): Future[UserId] = {
    val action = (for {
      id <- userEntities.createUser(username)
      hashed = BCrypt.hashpw(password, BCrypt.gensalt(config.bcryptFactor))
      now = DateTime.now
      _ <- userPasswords += UserPassword(id, hashed, "bcrypt", now, None, true)
      _ <- userEmails += UserEmail(id, primaryEmail, true, true, None, None)
    } yield (id)).transactionally
    database.run(action)
  }

  def getUserEntityById(userId: UserId): Future[Option[UserEntity]] = database.run { userEntities.filter(_.id === userId).result.headOption }
  private def getUserEntityByIdentificationQuery(identification: String) = userEntities.filter { u =>
    u.username === identification || userEmails.byUser(u.id).filter(_.email === identification).exists
  }

  def getUserEntityByIdentification(identification: String): Future[Option[UserEntity]] = database.run(getUserEntityByIdentificationQuery(identification).result.headOption)

  def authenticate(identification: String, password: String): Future[AuthenticationResult] = {
    // an OptionT for dbioActions would be awesome
    val action = getUserEntityByIdentificationQuery(identification).result.headOption flatMap { maybeUser =>
      maybeUser.map { user =>
        userPasswords.getUserCurrentPassword(user.id).map(p => (p.hashedPassword, p.expiresAt)).result.head.map {
          case (hashed, expires) =>
            // TODO handle expiration
            if (BCrypt.checkpw(password, hashed)) {
              if (user.isActive) AuthenticationSuccess(user.id) else AuthenticationNotActive(user.id)
            } else AuthenticationFailure
        }
      } getOrElse (DBIO.successful(AuthenticationFailure))
    }
    database.run(action)
  }

  def activateUser(userId: UserId): Future[Boolean] = database.run(userEntities.ativateUser(userId).map(_ > 0))

  override def tables = super[UsersComponent].tables ++ super[AuthorizationComponent].tables
}

object Dal {
  def apply[DT <: JdbcDriver](driverP: DT, databaseP: DT#API#Database, configP: DalConfig)(implicit ec: ExecutionContext) = new Dal[DT] with DriverComponent[DT] with DatabaseComponent[DT] with SchemaManagement with DefaultExecutionContext {
    override val driver = driverP
    override val database = databaseP
    override val executionContext = ec
    override val config = configP
  }
}

trait DalComponent[DT <: JdbcDriver] {
  def dal: Dal[DT] with SchemaManagement with DefaultExecutionContext
}