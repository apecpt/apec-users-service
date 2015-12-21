package pt.org.apec.services.users.dal

import slick.driver.JdbcDriver
import godiva.slick._
import pt.org.apec.services.users.common._
import org.joda.time.DateTime

trait AuthorizationComponent[DT <: JdbcDriver] extends TablesSchema {
  this: DriverComponent[DT] with JodaSupport[DT] with UsersComponent[DT] =>
  import driver.api._
  import Joda._

  class Roles(tag: Tag) extends Table[Role](tag, "roles") {
    def id = column[RoleId]("id", O.PrimaryKey)
    def name = column[String]("name")
    def nameIndex = index("role_name_idx", name, true)
    def createdAt = column[DateTime]("created_at")
    def notes = column[Option[String]]("notes")
    def * = (id, name, createdAt, notes) <> (Role.tupled, Role.unapply _)
  }

  object roles extends TableQuery(new Roles(_)) {
    val findById = this.findBy(_.id)
  }

  class Permissions(tag: Tag) extends Table[Permission](tag, "permissions") {
    def id = column[PermissionId]("id", O.PrimaryKey)
    def name = column[String]("name")
    def createdAt = column[DateTime]("created_at")
    def notes = column[Option[String]]("notes")
    def * = (id, name, createdAt, notes) <> (Permission.tupled, Permission.unapply)
  }

  object permissions extends TableQuery(new Permissions(_)) {
    val findById = this.findBy(_.id)
  }

  class RolePermissions(tag: Tag) extends Table[(RoleId, PermissionId)](tag, "role_permissions") {
    def roleId = column[RoleId]("role_id")
    def permissionId = column[PermissionId]("permission_id")
    def role = foreignKey("role_fk", roleId, roles)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def permission = foreignKey("permission_fk", permissionId, permissions)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def rolePermissionIndex = index("role_permission_idx", (roleId, permissionId), true)
    def * = (roleId, permissionId)
  }

  val rolePermissions = TableQuery[RolePermissions]

  class UserRoles(tag: Tag) extends Table[(UserId, RoleId)](tag, "user_roles") {
    def userId = column[UserId]("user_id")
    def roleId = column[RoleId]("role_id")
    def user = foreignKey("user_fk", userId, userEntities)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def role = foreignKey("role_fk", roleId, roles)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def userRoleIndex = index("user_roles_idx", (userId, roleId), true)
    def * = (userId, roleId)
  }

  val userRoles = TableQuery[UserRoles]

  class UserPermissions(tag: Tag) extends Table[(UserId, PermissionId)](tag, "user_permissions") {
    def userId = column[UserId]("user_id")
    def permissionId = column[RoleId]("permission_id")
    def user = foreignKey("user_fk", userId, userEntities)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def permission = foreignKey("permission_fk", permissionId, permissions)(_.id, ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)
    def userPermissionIndex = index("user_permissions_idx", (userId, permissionId), true)
    def * = (userId, permissionId)
  }

  val userPermissions = TableQuery[UserPermissions]

  override def tables: Seq[TableQuery[_ <: Table[_]]] = Seq(roles, permissions, rolePermissions, userRoles, userPermissions)

}