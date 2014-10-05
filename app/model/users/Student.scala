package model.users

import org.bson.types.ObjectId

case class Student(id: ObjectId = new ObjectId,
                   email: String,
                   password: String) extends User

