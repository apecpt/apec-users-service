package pt.org.apec.services.users.dal

import slick.driver.JdbcDriver
import godiva.slick.DriverComponent

trait JodaSupport[DT >: JdbcDriver] {
  this : DriverComponent[JdbcDriver] =>
    object Joda extends com.github.tototoshi.slick.GenericJodaSupport(driver)
}