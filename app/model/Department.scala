package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import model.users.{Student, Teacher, User, DepartmentManager}


case class Department(id: ObjectId = new ObjectId,
                      name: String,
                      description: String)

object Department extends ModelCompanion[Department, ObjectId] {

  val collection = mongoCollection("department")
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
    User.find(MongoDBObject("departmentId" -> new ObjectId(id), "_t" -> "model.users.DepartmentManager")).toList.asInstanceOf[List[DepartmentManager]]
  }

  def getTeachers(id: String): List[Teacher] = {
    User.find(MongoDBObject("departmentId" -> new ObjectId(id), "_t" -> "model.users.Teacher"))
      .toList.asInstanceOf[List[Teacher]]
  }

  def getAllStudents(filter: StudentFilter): List[Student] = {
    val builder = MongoDBObject.newBuilder
    if(filter.fullName != ""){
      val regexp = (filter.fullName+".*").r
      builder += "fullName" -> regexp
    }
    if(filter.email != ""){
      val regexp = (filter.email+".*").r
      builder += "email" -> regexp
    }
    if(filter.group != null){
      builder += "group" -> filter.group
    }
    if(filter.status != ""){
      builder += "status" -> filter.status
    }
    builder += "_t" -> "model.users.Student"
    User.find(builder.result()).toList.asInstanceOf[List[Student]].sortBy(_.fullName)
  }

  def getAllGroups: List[Group] = {
    Group.findAll().toList
  }
}

case class StudentFilter( fullName: String = "",
                          email: String = "",
                          status: String = "",
                          group: ObjectId = null,
                          departmentId: ObjectId = null)
