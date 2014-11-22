package model.users

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import model.mongoContext
import mongoContext._
import com.mongodb.casbah.Imports
import org.jasypt.util.text.BasicTextEncryptor

trait User {
  val id: ObjectId
  val email: String
  var password: String
  var theme: String
  var avatar: String
}

object User extends ModelCompanion[User, ObjectId] {
  val collection = mongoCollection("users")
  val dao = new SalatDAO[User, ObjectId](collection = collection) {}
  val textEncryptor = new BasicTextEncryptor()
  textEncryptor.setPassword("secret")

  def findByEmail(email: String): Option[User] = {
    val user = findOne(MongoDBObject("email" -> email))
    if(!user.isEmpty){
      user.get.password = textEncryptor.decrypt(user.get.password)
    }
    user
  }

  def find(id: String) = {
    val user = dao.findOneById(new ObjectId(id))
    if(!user.isEmpty){
      user.get.password = textEncryptor.decrypt(user.get.password)
    }
    user
  }


  override def insert(t: User): Option[Imports.ObjectId] = {
    t.password = textEncryptor.encrypt(t.password)
    super.insert(t)
  }


  override def save(t: User): Imports.WriteResult = {
    t.password = textEncryptor.encrypt(t.password)
    super.save(t)
  }

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
