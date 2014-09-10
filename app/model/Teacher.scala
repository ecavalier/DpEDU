package model

import com.novus.salat.dao.SalatDAO
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.MongoConnection
import play.api.Play._
import play.api.PlayException
import com.novus.salat.annotations.raw.Key
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.TypeImports.ObjectId

case class Teacher(@Key("_id") id: ObjectId = new ObjectId, email: String, password: String, fullName: String) extends
User(id, email, password, fullName) {
  var group: Group = _
}


