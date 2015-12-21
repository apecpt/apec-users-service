package pt.org.apec.services.users.dal

case class DalConfig(passwordExpirationDays: Int, emailExpirationDays: Int, bcryptFactor: Int)

