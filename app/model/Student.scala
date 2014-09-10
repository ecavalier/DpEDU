package model

import org.bson.types.ObjectId
import com.novus.salat.annotations.raw.Key

case class Student(@Key("_id") id: ObjectId = new ObjectId, email: String, password: String, fullName: String) extends
User(id, email, password, fullName) {

var group: Group = _
}
