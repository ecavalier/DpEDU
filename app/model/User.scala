package model

import com.novus.salat.annotations.raw.Key
import org.bson.types.ObjectId


abstract class User(@Key("_id") id: ObjectId = new ObjectId, email: String, password: String, fullName:String) {
  val isAdmin: Boolean = false
}


