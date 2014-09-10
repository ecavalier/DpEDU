package model

import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.novus.salat.dao.{ SalatDAO, ModelCompanion }
import com.mongodb.casbah.commons.TypeImports.ObjectId


case class Faculty(@Key("_id") id: ObjectId = new ObjectId,
                   name: String,
                   description: String)

object  Faculty extends ModelCompanion[Faculty, ObjectId] {
  val collection = MongoConnection()("dnu")("faculty")
  val dao = new SalatDAO[Faculty, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneByID(new ObjectId(id))

  def create(name: String, description: String): Option[ObjectId] = {
    dao.insert(Faculty(name = name, description=description))
  }

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

  def getDepartments(id: String): List[Department] =
    Department.dao.find(MongoDBObject("faculty_id" -> new ObjectId(id))).toList

  def getGroups(id: String): List[Group] =
    Group.dao.find("department_id" $in  getDepartments(id).map(_.id)).toList
}
