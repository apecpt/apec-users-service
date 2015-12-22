package pt.org.apec.services.users

import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver
import dal._
import akka.actor._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.io.IO
import spray.can.Http

object Boot extends App {
  import PostgresDriver.api.Database
  val logger = LoggerFactory.getLogger(getClass)
  val config = pureconfig.loadConfig[Config].get
  val db = Database.forConfig("db.default")

  this.args.toList match {
    case "initdb" :: Nil => {
      import scala.concurrent.ExecutionContext.Implicits.global
      val dal = Dal(PostgresDriver, db, config.dal)

      logger.info("Initializing database.")
      Await.result(dal.createTablesIfNotExist, 5 seconds)
      logger.info("finished.")
    }
    case Nil => {
      implicit val system = ActorSystem("usersservice")
      import system.dispatcher
      val dal = Dal(PostgresDriver, db, config.dal)
      val service = system.actorOf(Props(new UsersServiceActor(dal)), "usersService")
      logger.info(s"Binding Http service to ${config.http.interface}:${config.http.port}")
      IO(Http) ! Http.Bind(service, interface = config.http.interface, port = config.http.port)

    }
  }

}