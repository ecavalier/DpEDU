package model.users

import com.mongodb.casbah.commons.TypeImports.ObjectId


object TeacherPosition extends Enumeration {
  type TeacherPosition = Value
  val SeniorTeacher, Docent, Professor = Value
}

case class Teacher(id: ObjectId = new ObjectId,
                   email: String,
                   var password: String ="",
                   var theme: String = "bootstrap.min.css",
                   fullName: String,
                   position: String,
                   departmentId: ObjectId,
                   var avatar: String = "@routes.Assets.at(\"images/anonymous.jpg\")") extends User


