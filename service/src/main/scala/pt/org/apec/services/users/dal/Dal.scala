package pt.org.apec.services.users.dal

import scala.concurrent.ExecutionContext
import slick.driver.JdbcDriver
import godiva.slick._

trait Dal[DT <: JdbcDriver] extends JodaSupport[DT] with UsersComponent[DT] with AuthorizationComponent[DT] with TablesSchema  {
  this: DriverComponent[DT] with DatabaseComponent[DT] with DefaultExecutionContext with SchemaManagement =>
    
    override def tables = super[UsersComponent].tables ++ super[AuthorizationComponent].tables
}

object Dal {
  def aplly[DT <: JdbcDriver](driverP: DT, databaseP: DT#API#Database)(implicit ec: ExecutionContext) = new Dal[DT] with DriverComponent[DT] with DatabaseComponent[DT] with SchemaManagement with DefaultExecutionContext {
    override val driver = driverP
    override val database = databaseP
    override val executionContext = ec
  }
}