package pt.org.apec.services.users.common

import org.joda.time.DateTime

case class Role(id: RoleId, name: String, createdAt: DateTime, notes: Option[String])

case class Permission(id: PermissionId, name: String, createdAt: DateTime, notes: Option[String])

