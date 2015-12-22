package pt.org.apec.services.users.common.json

import play.api.libs.json._
import play.api.libs.functional.syntax._
import pt.org.apec.services.users.common._

trait JsonProtocol {
  implicit val userREgistrationFormat = Json.format[UserRegistration]
  implicit val userCredentialsFormat = Json.format[UserCredentials]

  implicit val authenticationResultReads: Reads[AuthenticationResult] = {
    implicit val authenticationSuccessReads = Json.reads[AuthenticationSuccess]
    implicit val authenticationPasswordExpiredReads = Json.reads[AuthenticationPasswordExpired]
    implicit val authenticationNotActiveReads = Json.reads[AuthenticationNotActive]
    (__ \ "success").read[AuthenticationSuccess].map(_.asInstanceOf[AuthenticationResult]) |
      (__ \ "failure").read(AuthenticationFailure.asInstanceOf[AuthenticationResult]) |
      (__ \ "notActive").read[AuthenticationNotActive].map(_.asInstanceOf[AuthenticationResult]) |
      (__ \ "passwordExpired").read[AuthenticationPasswordExpired].map(_.asInstanceOf[AuthenticationResult])
  }
  implicit val authenticationResultWrites = Writes[AuthenticationResult] { result =>
    implicit val authenticationSuccessWrites = Json.writes[AuthenticationSuccess]
    implicit val authenticationNotActiveWrites = Json.writes[AuthenticationNotActive]
    implicit val authenticationPasswordExpiredWrites = Json.writes[AuthenticationPasswordExpired]
    result match {
      case s: AuthenticationSuccess => Writes.at[AuthenticationSuccess](__ \ "success").writes(s)
      case n: AuthenticationNotActive => Writes.at[AuthenticationNotActive](__ \ "notActive").writes(n)
      case e: AuthenticationPasswordExpired => Writes.at[AuthenticationPasswordExpired](__ \ "passwordExpired").writes(e)
      case AuthenticationFailure => Writes.at[JsValue](__ \ "failure").writes(JsObject.apply(Seq.empty))
    }
  }

  implicit val userRegistrationSuccessFormat = Json.format[UserRegistrationSuccess]
  implicit val userRegistrationResultWrites: Writes[UserRegistrationResult] = Writes[UserRegistrationResult] { result =>
    result match {
      case s: UserRegistrationSuccess => userRegistrationSuccessFormat.writes(s)
      case _ => ???
    }
  }
}