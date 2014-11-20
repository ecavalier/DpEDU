package model

import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import mongoContext._
import org.joda.time.DateTime
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers

case class Feed(id: ObjectId = new ObjectId,
                author: ObjectId,
                date: DateTime,
                text: String)

object Feed extends ModelCompanion[Feed, ObjectId] {
  RegisterJodaTimeConversionHelpers()
  val collection = mongoCollection("news")
  val dao = new SalatDAO[Feed, ObjectId](collection = collection) {}

  def find(id: String) = dao.findOneById(new ObjectId(id))

  def delete(id: String) =  dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
}
