package pt.org.apec.services.users.dal

import slick.driver.JdbcDriver
import godiva.slick.DriverComponent

trait JodaSupport[DT <: JdbcDriver] {
  this: DriverComponent[DT] =>
  object Joda extends com.github.tototoshi.slick.GenericJodaSupport(driver)
}