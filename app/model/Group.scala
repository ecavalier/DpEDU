package model

import model.CustomPlaySalatContext
import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}


@Salat
case class Group(@Key("_id") id: ObjectId = new ObjectId,
                 name: String,
                 departmentId: ObjectId,
                 elder: ObjectId,
                 curator: ObjectId)


object Group extends ModelCompanion[Group, ObjectId] {

  val collection = MongoConnection()("dnu")("groups")
  val dao = new SalatDAO[Group, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

  def create(name: String, departmentId: ObjectId): Option[ObjectId] = {
    dao.insert(Group(name = name, elder = null, curator = null, departmentId = departmentId))
  }

  def getStudentsByGroupId{}
}

