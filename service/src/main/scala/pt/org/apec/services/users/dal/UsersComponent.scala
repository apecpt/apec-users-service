package pt.org.apec.services.users.dal

import godiva.slick._
import slick.driver.JdbcDriver
import pt.org.apec.services.users.common._
import org.joda.time.DateTime

trait UsersComponent[DT <: JdbcDriver] extends TablesSchema {
  this: DriverComponent[DT] with JodaSupport[DT] =>
  import driver.api._
  import Joda._

  class UserEntities(tag: Tag) extends Table[UserEntity](tag, "user_entities") {
    def id = column[UserId]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[Option[DateTime]]("updated_at")
    def isActive = column[Boolean]("is_active", O.Default(false))
    def usernameIndex = index("username_idx", username, true)
    def * = (id, username, createdAt, updatedAt, isActive) <> (UserEntity.tupled, UserEntity.unapply _)
  }

  object userEntities extends TableQuery(new UserEntities(_)) {
    def activeUsers = this.filter(_.isActive === true)
    def createUser(username: String) = (map(t => (t.username, t.createdAt)) returning map(_.id)) += (username, DateTime.now)
    def ativateUser(userId: UserId) = filter(_.id === userId).map(_.isActive).update(true)
  }

  class UserPasswords(tag: Tag) extends Table[UserPassword](tag, "user_passwords") {
    def userId = column[UserId]("user_id")
    def user = foreignKey("user_fk", userId, userEntities)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def hashedPassword = column[String]("hashed_password")
    def method = column[String]("method")
    def createdAt = column[DateTime]("created_at")
    def expiresAt = column[Option[DateTime]]("expires_at")
    def active = column[Boolean]("active")
    def activeIndex = index("password_active_idx", active, true)
    def * = (userId, hashedPassword, method, createdAt, expiresAt, active) <> (UserPassword.tupled, UserPassword.unapply _)
  }

  object userPasswords extends TableQuery(new UserPasswords(_)) {
    def getUserCurrentPassword(userId: UserId) = filter(_.userId === userId).filter(_.active === true)
    def activePasswords = filter(_.active === true)
  }

  class UserProfiles(tag: Tag) extends Table[UserProfile](tag, "user_profiles") {
    def userId = column[UserId]("user_id")
    def user = foreignKey("user_id_profile_fk", userId, userEntities)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def userIdIndex = index("user_id_idx", userId, true)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def displayName = column[String]("display_name")
    def country = column[String]("country")
    def manualVerification = column[Boolean]("manual_verification", O.Default(false))
    def verificationDocumentUrl = column[Option[String]]("verification_document")
    def verifiedAt = column[Option[DateTime]]("verified_at")
    def * = (userId, firstName, lastName, displayName, country, manualVerification, verificationDocumentUrl, verifiedAt) <> (UserProfile.tupled, UserProfile.unapply _)
  }
  object userProfiles extends TableQuery(new UserProfiles(_)) {
    lazy val byUser = this.findBy(_.userId)
  }

  class UserEmails(tag: Tag) extends Table[UserEmail](tag, "user_emails") {
    def userId = column[UserId]("user_id")
    def user = foreignKey("user_id_email_fk", userId, userEntities)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def email = column[String]("email")
    def emailIndex = index("user_email_idx", email, true)
    def primary = column[Boolean]("primary")
    def verified = column[Boolean]("verified")
    def verificationToken = column[Option[String]]("verification_token")
    def requestDate = column[Option[DateTime]]("request_date")
    def * = (userId, email, primary, verified, verificationToken, requestDate) <> (UserEmail.tupled, UserEmail.unapply _)
  }

  object userEmails extends TableQuery(new UserEmails(_)) {
    def byUser(userId: Rep[UserId]) = this.filter(_.userId === userId).filter(_.verified === true)
  }

  override def tables: Seq[TableQuery[_ <: Table[_]]] = Seq(userEntities, userPasswords, userProfiles, userEmails)
}