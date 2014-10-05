package model.users

import org.bson.types.ObjectId

case class DepartmentManager (id: ObjectId = new ObjectId,
                              email: String,
                              password: String) extends User
