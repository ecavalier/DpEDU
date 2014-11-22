package controllers.dean

import model.{Department}
import org.bson.types.ObjectId
import play.api.data.Form
import play.api.data.Forms._
import model.StudentFilter

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

}
