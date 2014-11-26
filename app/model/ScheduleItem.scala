package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import model.users.{Teacher, User, Student}


case class ScheduleItem(id: ObjectId = new ObjectId,
                   lecture: Int,
                   day: Int,
                   week: Int,
                   group: ObjectId,
                   subject: ObjectId,
                   room: ObjectId)

object ScheduleItem extends ModelCompanion[ScheduleItem, ObjectId] {
  val collection = mongoCollection("schedule")
  val dao = new SalatDAO[ScheduleItem, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))

  def find(groupId: ObjectId, lecture: Int, day: Int, week: Int): Option[ScheduleItem] = {
    dao.findOne(MongoDBObject("group"->groupId, "day" -> day, "week" -> week, "lecture" -> lecture))
  }

  def findForTeacher(teacherId: ObjectId, lecture: Int, day: Int, week: Int): Option[ScheduleItem] = {
    val ids = Subject.find(MongoDBObject("teacher"->teacherId)).toList.map(_.id)
    dao.findOne(MongoDBObject("subject"-> DBObject("$in"->ids), "day" -> day,
      "week" -> week,
      "lecture" -> lecture))
  }

  def filterTeacher(filter: TeacherFilter): List[ScheduleItem] = {
    val builder = MongoDBObject.newBuilder
    builder += "day" -> filter.day
    builder += "week" -> filter.week
    builder +=  "subject"-> DBObject("$in"->Subject.find(MongoDBObject("teacher"->filter.teacherId)).toList.map(_.id))
    ScheduleItem.find(builder.result()).toList
  }
}

case class TeacherFilter( teacherId: ObjectId,
                          day: Int,
                          week: Int)
