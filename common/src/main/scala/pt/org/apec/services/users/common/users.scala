package pt.org.apec.services.users.common
import org.joda.time.DateTime

case class UserEntity(id: UserId, username: String, password: Array[Byte], createdAt: DateTime, updatedAt: Option[DateTime], isActive: Boolean)

case class UserProfile(userId: UserId, firstName: String, lastName: String, displayName: String, country: String, manualVerification:Boolean, verificationDocumentUrl: Option[String], verifiedAt: Option[DateTime])

case class UserEmail(email: String, primary: Boolean, verified: Boolean, verificationToken: Option[String], requestDate: Option[DateTime])

