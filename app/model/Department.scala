package model

import model.CustomPlaySalatContext
import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import model.users._


case class Department(@Key("_id") id: ObjectId = new ObjectId,
                      name: String,
                      description: String)

object Department extends ModelCompanion[Department, ObjectId] {

  val collection = MongoConnection()("dnu")("department")
  val dao = new SalatDAO[Department, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def create(name: String, description: String): Option[ObjectId] = {
    dao.insert(Department(name = name, description = description))
  }

  def delete(id: String) {
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

  def getGroups(id: String): List[Group] = {
    Group.find(MongoDBObject("departmentId" -> new ObjectId(id))).toList
  }

  def getManagers(id: String): List[DepartmentManager] = {
    User.find(MongoDBObject("departmentId" -> new ObjectId(id), "_typeHint" -> "model.users.DepartmentManager")).toList.asInstanceOf[List[DepartmentManager]]
  }

  def getTeachers(id: String): List[Teacher] = {
    User.find(MongoDBObject("departmentId" -> new ObjectId(id), "_typeHint" -> "model.users.Teacher"))
      .toList.asInstanceOf[List[Teacher]]
  }

  def getAllStudents(filter: StudentFilter): List[Student] = {
    val builder = MongoDBObject.newBuilder
    if(filter.fullName != null){
      builder += "fullName" -> filter.fullName
    }
    if(filter.email != null){
      builder += "email" -> filter.email
    }
    if(filter.group != null){
      builder += "group" -> filter.group
    }
    if(filter.status != null){
      builder += "filter.status" -> filter.status
    }
    builder += "_typeHint" -> "model.users.Student"
    User.find(builder).toList.asInstanceOf[List[Student]]
  }
}

case class StudentFilter( fullName: String,
                          email: String,
                          status: String,
                          group: ObjectId,
                          departmentId: ObjectId)
