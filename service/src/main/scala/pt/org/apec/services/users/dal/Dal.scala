package pt.org.apec.services.users.dal

import scala.concurrent.ExecutionContext
import slick.driver.JdbcDriver
import godiva.slick._
import org.mindrot.jbcrypt.BCrypt
import org.joda.time.DateTime
import pt.org.apec.services.users.common._
import scala.concurrent.Future

trait Dal[DT <: JdbcDriver] extends JodaSupport[DT] with UsersComponent[DT] with AuthorizationComponent[DT] with TablesSchema {
  this: DriverComponent[DT] with DatabaseComponent[DT] with DefaultExecutionContext with SchemaManagement =>
  import driver.api._

  def config: DalConfig

  // user related methods
  // make email verification working.
  def createUser(username: String, password: String, primaryEmail: String): Future[UserId] = {
    val action = (for {
      id <- userEntities.createUser(username)
      val hashed = BCrypt.hashpw(password, BCrypt.gensalt(config.bcryptFactor))
      val now = DateTime.now
      _ <- userPasswords += UserPassword(id, hashed, "bcrypt", now, None, true)
      _ <- userEmails += UserEmail(id, primaryEmail, true, false, None, None)
    } yield (id)).transactionally
    database.run(action)

  }

  override def tables = super[UsersComponent].tables ++ super[AuthorizationComponent].tables
}

object Dal {
  def aplly[DT <: JdbcDriver](driverP: DT, databaseP: DT#API#Database, configP: DalConfig)(implicit ec: ExecutionContext) = new Dal[DT] with DriverComponent[DT] with DatabaseComponent[DT] with SchemaManagement with DefaultExecutionContext {
    override val driver = driverP
    override val database = databaseP
    override val executionContext = ec
    override val config = configP
  }
}