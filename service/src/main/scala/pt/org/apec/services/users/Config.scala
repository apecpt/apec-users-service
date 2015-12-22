package pt.org.apec.services.users
import dal.DalConfig

case class HttpConfig(interface: String, port: Int)

case class Config(http: HttpConfig, dal: DalConfig)