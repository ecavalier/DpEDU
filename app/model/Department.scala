package model

import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}


case class Department(@Key("_id") id: ObjectId = new ObjectId,
                      name: String,
                      @Key("faculty_id") facultyId: ObjectId)

object  Department extends ModelCompanion[Department, ObjectId] {

  val collection = MongoConnection()("dnu")("department")
  val dao = new SalatDAO[Department, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneByID(new ObjectId(id))

  def create(name: String, facultyId : String): Option[ObjectId] = {
    dao.insert(Department(name = name, facultyId = new ObjectId(facultyId)))
  }

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

  def getFaculty(id: String) = Faculty.find(find(id).get.facultyId.toString)

}
