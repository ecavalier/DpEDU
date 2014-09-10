package model

import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}

case class Group(@Key("_id") id: ObjectId = new ObjectId,
                 name: String,
                 @Key("department_id") departmentId: ObjectId)


object  Group extends ModelCompanion[Group, ObjectId] {

  val collection = MongoConnection()("dnu")("groups")
  val dao = new SalatDAO[Group, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneByID(new ObjectId(id))

  def create(name: String, departmentId : String): Option[ObjectId] = {
    dao.insert(Group(name = name, departmentId = new ObjectId(departmentId)))
  }

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

}

