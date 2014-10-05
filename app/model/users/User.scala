package model.users

import model.CustomPlaySalatContext
import CustomPlaySalatContext._
import com.mongodb.casbah.Imports._
import com.novus.salat.annotations.raw.Salat
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import com.novus.salat.annotations._


@Salat
trait User{
  @Key("_id")val id: ObjectId
  val email: String
  val password: String
  var theme: String
}

object User extends ModelCompanion[User, ObjectId] {
val collection = MongoConnection()("dnu")("users")
val dao = new SalatDAO[User, ObjectId](collection = collection) {}
  def findByEmail(email: String): Option[User] = findOne(DBObject("email" -> email))
  def find(id: String) = dao.findOneById(new ObjectId(id))
}



