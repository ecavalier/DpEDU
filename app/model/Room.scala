package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._

object RoomType extends Enumeration {
  type RoomType = Value
  val Lecture, Practice, Lab, Other = Value
  def getByName(name: String): RoomType = {
    name match{
      case "Lecture" => RoomType.Lecture
      case "Practice" => RoomType.Practice
      case "Lab" => RoomType.Lab
      case "Other" => RoomType.Other
    }
  }
}

case class Room(id: ObjectId = new ObjectId,
                 name: String,
                 roomType: String = RoomType.Lecture.toString,
                 picture: String = "")

object Room extends ModelCompanion[Room, ObjectId] {
  val collection = mongoCollection("rooms")
  val dao = new SalatDAO[Room, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
