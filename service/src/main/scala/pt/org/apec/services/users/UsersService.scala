

package pt.org.apec.services.users

import spray.routing._
import pt.org.apec.services.users.common.json.JsonProtocol
import pt.org.apec.services.users.dal._
import spray.http.StatusCodes._
import pt.org.apec.services.users.common._
import spray.httpx.PlayJsonSupport
import scala.concurrent.ExecutionContext

trait UsersService extends HttpService with PlayJsonSupport with JsonProtocol with DalComponent {
  implicit val executionContext: ExecutionContext
  def userRoutes = path("users") {
    path("register") {
      (post & entity(as[UserRegistration])) { registration =>
        complete {
          Created -> dal.registerUser(registration)
        }
      }
    } ~
      path(LongNumber) { id =>
        path("activate") {
          post {
            complete {
              dal.activateUser(id) map { result =>
                if (result) OK else Accepted
              }
            }
          }
        }
      }
  }
}