package model.users

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import model.mongoContext
import mongoContext._

trait User {
  val id: ObjectId
  val email: String
  val password: String
  var theme: String
  var avatar: String
}

object User extends ModelCompanion[User, ObjectId] {
  val collection = mongoCollection("users")
  val dao = new SalatDAO[User, ObjectId](collection = collection) {}

  def findByEmail(email: String): Option[User] = findOne(MongoDBObject("email" -> email))

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
