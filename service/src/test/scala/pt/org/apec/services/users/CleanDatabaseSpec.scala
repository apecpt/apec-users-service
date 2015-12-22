package pt.org.apec.services.users

import scala.concurrent.Await
import scala.concurrent.duration._

import org.scalatest._

import slick.driver.H2Driver
import dal._
import scala.concurrent.ExecutionContext.Implicits.global

trait CleanDatabaseSpec extends DalComponent[H2Driver.type] with BeforeAndAfterEach {
  this: Suite =>
  private lazy val db = H2Driver.api.Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver", keepAliveConnection = true)
  val config = DalConfig(1, 1, 12)
  override lazy val dal = Dal(H2Driver, db, config)
  override def beforeEach() {
    super.beforeEach
    Await.result(dal.dropTablesIfExist, 5 seconds)
    Await.result(dal.createTables, 5 seconds)
  }

}
