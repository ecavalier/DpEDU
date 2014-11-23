package controllers.teacher

import model.users.User
import java.io._
import org.bson.types.ObjectId
import model.users.Student
import model.users.DeanManager
import model.users.Teacher
import org.apache.commons.io.IOUtils
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper

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
    val writer = PdfWriter.getInstance(document, stream)

    document.open()

    val is = new ByteArrayInputStream(views.html.profile.teacher.shedule.pdf(
      new ObjectId(group), week.toInt).toString().getBytes)
    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is)

    document.close()
    val outputStream = new ByteArrayInputStream(stream.toByteArray)
    val out = new FileOutputStream(f)
    IOUtils.copy(outputStream, out)

    Ok.sendFile(f)
  }

}
