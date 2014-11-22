package controllers.dean

import model.ScheduleItem
import org.bson.types.ObjectId
import play.api.data.Form
import play.api.data.Forms._
import com.lowagie.text.{Element, Document}
import com.lowagie.text.pdf.PdfWriter
import java.io._
import model.users.Student
import model.users.DeanManager
import model.users.Teacher
import org.apache.commons.io.IOUtils
import com.lowagie.text.html.simpleparser.HTMLWorker
import scala.collection.JavaConverters._


object ScheduleController extends DeanController {

  val scheduleForm = Form{
    tuple(
      "subject" -> text,
      "group" -> text,
      "room" -> text,
      "lecture" -> text,
      "day" -> text,
      "week" -> text
    )
  }


  def openSchedule() = withUser(Array[Class[_]](DeanManager.getClass, Student.getClass)) { user => implicit request =>
    changeView(views.html.profile.dean.shedule.view("", ""))
  }

  def getScheduleForm() = withUser { user => implicit request =>
    val lecture = request.getQueryString("lecture").get
    val week = request.getQueryString("week").get
    val group = request.getQueryString("group").get
    val day = request.getQueryString("day").get
    val item = ScheduleItem.find(new ObjectId(group), lecture.toInt, day.toInt, week.toInt)
    if(item.isEmpty){
      Ok(views.html.profile.dean.shedule.form("Create Item", "form", "Create" ,null,
        new ObjectId(group), lecture.toInt, day.toInt, week.toInt , routes.ScheduleController.addScheduleItem()))
    }else{
      Ok(views.html.profile.dean.shedule.form("Edit Item", "form", "Update" ,item.get,
        new ObjectId(group), lecture.toInt, day.toInt, week.toInt ,  routes.ScheduleController.saveScheduleItem(item.get.id.toString)))
    }
  }

  def addScheduleItem() = withUser { user => implicit request =>
    val (subject, group, room, lecture, day, week) =  scheduleForm.bindFromRequest().get
    ScheduleItem.insert(ScheduleItem(subject = new ObjectId(subject), group = new ObjectId(group),
      room = new ObjectId(room), lecture = lecture.toInt, day = day.toInt, week = week.toInt))
    changeView(views.html.profile.dean.shedule.view(group, week))
  }

  def saveScheduleItem(id: String) = withUser { user => implicit request =>
    val (subject, group, room, lecture, day, week) =  scheduleForm.bindFromRequest().get
    val item = ScheduleItem.find(id).get
    ScheduleItem.save(item.copy(subject = new ObjectId(subject), group = new ObjectId(group),
      room = new ObjectId(room), lecture = lecture.toInt, day = day.toInt, week = week.toInt))
    changeView(views.html.profile.dean.shedule.view(group, week))
  }

  def getShowTable() = withUser(Array[Class[_]](DeanManager.getClass, Student.getClass)) { user => implicit request =>
    val group = request.getQueryString("group").get
    val week = request.getQueryString("week").get
    Ok(views.html.profile.dean.shedule.schedule(new ObjectId(group), week.toInt))
  }

  def schedulePDF(group: String, week: String) = withUser(Array[Class[_]](DeanManager.getClass, Student.getClass,
    Teacher.getClass)) { user => implicit request =>
      val document = new Document()
      val stream =  new ByteArrayOutputStream()
      val f=File.createTempFile("schedule", ".pdf")
      PdfWriter.getInstance(document, stream)
      document.open()
      val objects = HTMLWorker.parseToList(
        new StringReader(views.html.profile.dean.shedule.pdf(
          new ObjectId(group), week.toInt).toString()), null).asScala

        for (element <- objects){
            document.add(element.asInstanceOf[Element])
          }

      document.close()
      val outputStream = new ByteArrayInputStream(stream.toByteArray)

      val out = new FileOutputStream(f)
      IOUtils.copy(outputStream, out);

      Ok.sendFile(f)
  }
}
