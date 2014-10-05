package model

import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import model.users.{Teacher, Student}

case class Group(@Key("_id") id: ObjectId = new ObjectId,
                 name: String,
                 students: List[Student],
                 elder: Student,
                 curator: Teacher)


object  Group extends ModelCompanion[Group, ObjectId] {

  val collection = MongoConnection()("dnu")("groups")
  val dao = new SalatDAO[Group, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneByID(new ObjectId(id))

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }
}

