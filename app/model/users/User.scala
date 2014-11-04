package model.users

import model.CustomPlaySalatContext
import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}

@Salat
trait User {
  @Key("_id") val id: ObjectId
  val email: String
  val password: String
  var theme: String
  var avatar: String
}

object User extends ModelCompanion[User, ObjectId] {
  val collection = MongoConnection()("dnu")("users")
  val dao = new SalatDAO[User, ObjectId](collection = collection) {}

  def findByEmail(email: String): Option[User] = findOne(MongoDBObject("email" -> email))

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
