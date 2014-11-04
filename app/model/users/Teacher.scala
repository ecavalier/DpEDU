package model.users

import com.mongodb.casbah.commons.TypeImports.ObjectId


object TeacherPosition extends Enumeration {
  type TeacherPosition = Value
  val A, B, C, D = Value
}

case class Teacher(id: ObjectId = new ObjectId,
                   email: String,
                   password: String,
                   var theme: String = "bootstrap.min.css",
                   position: String,
                   departmentId: ObjectId,
                   var avatar: String = "@routes.Assets.at(\"images/anonymous.jpg\")") extends User


