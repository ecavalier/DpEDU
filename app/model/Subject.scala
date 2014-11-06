package model

import model.CustomPlaySalatContext
import CustomPlaySalatContext._
import com.novus.salat.annotations._
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.TypeImports._

object SubjectType extends Enumeration {
  type SubjectType = Value
  val Lecture, Practice, Lab = Value
}

case class Subject(@Key("_id") id: ObjectId = new ObjectId,
                    name: String,
                    teacher: ObjectId,
                    group: ObjectId,
                    subjectType: List[String])


object Subject extends ModelCompanion[Subject, ObjectId] {
  val collection = MongoConnection()("dnu")("subjects")
  val dao = new SalatDAO[Subject, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
