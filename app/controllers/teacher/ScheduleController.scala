package controllers.teacher

import model.users.{Student, DeanManager, Teacher, User}
import com.lowagie.text.{Element, Document}
import java.io._
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.html.simpleparser.HTMLWorker
import org.bson.types.ObjectId
import model.users.Student
import model.users.DeanManager
import model.users.Teacher
import org.apache.commons.io.IOUtils
import scala.collection.JavaConverters._

object ScheduleController extends TeacherController{

  def viewSchedule() = withUser { user => implicit request =>
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Teacher]
    changeView(views.html.profile.teacher.shedule.view(user.id.toString, "0"))
  }

  def getShowTable() = withUser { user => implicit request =>
    val week = request.getQueryString("week").get
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Teacher]
    Ok(views.html.profile.teacher.shedule.schedule(user.id, week.toInt))
  }

  def schedulePDF(group: String, week: String) = withUser(Array[Class[_]](DeanManager.getClass, Student.getClass,
    Teacher.getClass)) { user => implicit request =>
    val document = new Document()
    val stream =  new ByteArrayOutputStream()
    val f=File.createTempFile("schedule", ".pdf")
    PdfWriter.getInstance(document, stream)
    document.open()
    val objects = HTMLWorker.parseToList(
      new StringReader(views.html.profile.teacher.shedule.pdf(
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
