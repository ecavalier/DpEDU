package model.users

import com.mongodb.casbah.commons.TypeImports.ObjectId

case class Teacher(id: ObjectId = new ObjectId,
                   email: String,
                   password: String) extends User


