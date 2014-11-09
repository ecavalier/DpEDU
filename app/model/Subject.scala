package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

object SubjectType extends Enumeration {
  type SubjectType = Value
  val Lecture, Practice, Lab = Value
}

case class Subject( id: ObjectId = new ObjectId,
                    name: String,
                    teacher: ObjectId,
                    group: ObjectId,
                    subjectType: List[String])


object Subject extends ModelCompanion[Subject, ObjectId] {
  val collection = mongoCollection("subjects")
  val dao = new SalatDAO[Subject, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
