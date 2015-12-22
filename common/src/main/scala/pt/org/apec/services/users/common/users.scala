package pt.org.apec.services.users.common
import org.joda.time.DateTime

case class UserEntity(id: UserId, username: String, createdAt: DateTime, updatedAt: Option[DateTime], isActive: Boolean)

case class UserPassword(userId: UserId, hashedPassword: String, method: String, createdAt: DateTime, expiresAt: Option[DateTime], active: Boolean)

case class UserProfile(userId: UserId, firstName: String, lastName: String, displayName: String, country: String, manualVerification: Boolean, verificationDocumentUrl: Option[String], verifiedAt: Option[DateTime])

case class UserEmail(userId: UserId, email: String, primary: Boolean, verified: Boolean, verificationToken: Option[String], requestDate: Option[DateTime])

sealed trait AuthenticationResult
case class AuthenticationSuccess(id: UserId) extends AuthenticationResult
case class AuthenticationPasswordExpired(id: UserId) extends AuthenticationResult
case object AuthenticationFailure extends AuthenticationResult
case class AuthenticationNotActive(userId: UserId) extends AuthenticationResult
