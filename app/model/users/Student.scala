package model.users

import org.bson.types.ObjectId
import model.users.StudentStatus.StudentStatus


object StudentStatus extends Enumeration {
  type StudentStatus = Value
  val Active, Removed, In_Weekend = Value
}

case class Student(id: ObjectId = new ObjectId,
                   email: String,
                   password: String,
                   var theme: String =  "bootstrap.min.css",
                   isElder: Boolean,
                   status: StudentStatus) extends User

