package controllers.dean

import model.{Department}
import org.bson.types.ObjectId
import play.api.data.Form
import play.api.data.Forms._
import model.StudentFilter
import model.users.{StudentStatus, Student, User}

object StudentSearchController extends DeanController {

  val studentSearchForm = Form{
    tuple(
      "fullName" -> text,
      "email" -> text,
      "status" -> text,
      "group" -> text
    )
  }

  def searchStudent() = withUser { user => implicit request =>
    changeView(views.html.profile.dean.student.list(new model.StudentFilter()))
  }

  def searchStudentRender() = withUser { user => implicit request =>
    val group = request.getQueryString("group").get
    if(group!=""){
      val list = Department.getAllStudents(StudentFilter(fullName = request.getQueryString("fullName").get,
        status = request.getQueryString("status").get, email = request.getQueryString("email").get,
        group = new ObjectId(request.getQueryString("group").get)))
      Ok(views.html.profile.dean.student.tableContent(list))
    }else{
      val list = Department.getAllStudents(StudentFilter(fullName = request.getQueryString("fullName").get,
        status = request.getQueryString("status").get, email = request.getQueryString("email").get,
        group = null) )
      Ok(views.html.profile.dean.student.tableContent(list))
    }
  }

  def removeStudent(id: String) =  withUser { user => implicit request =>
    val student = User.find(id).get.asInstanceOf[Student]
    User.save(student.copy(status = StudentStatus.Removed.toString))
    changeView(views.html.profile.dean.student.list(new model.StudentFilter()))
  }

  def restoreStudent(id: String) =  withUser { user => implicit request =>
    val student = User.find(id).get.asInstanceOf[Student]
    User.save(student.copy(status = StudentStatus.Active.toString))
    changeView(views.html.profile.dean.student.list(new model.StudentFilter()))
  }

}
