package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import model.users.{Student, User}


case class Group(id: ObjectId = new ObjectId,
                 name: String,
                 departmentId: ObjectId,
                 elder: ObjectId = null,
                 curator: ObjectId)


object Group extends ModelCompanion[Group, ObjectId] {

  val collection = mongoCollection("groups")
  val dao = new SalatDAO[Group, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) {
    User.remove(MongoDBObject("group" -> id))
    dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

  def create(name: String, departmentId: ObjectId): Option[ObjectId] = {
    dao.insert(Group(name = name, elder = null, curator = null, departmentId = departmentId))
  }

  def getStudentsByGroupId(id: ObjectId): List[Student] = {
    User.find(MongoDBObject("group" -> id)).toList.asInstanceOf[List[Student]].sortBy(_.fullName)
  }

  def getStudentsCount(id: ObjectId): Int = {
    User.find(MongoDBObject("group" -> id)).count
  }

  def getSubjects(id: ObjectId): List[Subject] = {
    Subject.find(MongoDBObject("group" -> id)).toList
  }
}

