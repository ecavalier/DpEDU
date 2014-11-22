package controllers.department

import model.users.User
import model.users
import controllers.Application
import play.api.data.Form
import play.api.data.Forms._
import model.users.DepartmentManager
import model.users.Teacher

object TeacherController extends DepartmentController{

  val teacherForm = Form(
    tuple(
      "fullName" -> text,
      "position" -> text,
      "email" -> text,
      "password" -> text
    )
  )

  def teacherList() = withUser { user => implicit request =>
    changeView(views.html.profile.department.teachers.list(User.findByEmail(session.get("username").get).get
      .asInstanceOf[DepartmentManager].departmentId.toString))
  }

  def addTeacher() = withUser { user => implicit request =>
    val (fullName, position, email, password) = teacherForm.bindFromRequest().get
    val departmentId  =
      users.User.findByEmail(session.get("username").get).get.asInstanceOf[users.DepartmentManager].departmentId
    val id = User.insert(Teacher(fullName=fullName, position=position, email=email, password=password,
      departmentId=departmentId))
    val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/", id.get.toString)
    if(filename != ""){
      val teacher = users.User.findByEmail(email).get.asInstanceOf[users.Teacher]
      User.save(teacher.copy(avatar = filename))
    }
    Redirect(routes.TeacherController.teacherList())
  }

  def getTeacherEditForm(id: String) = withUser { user => implicit request =>
    Ok(views.html.profile.department.teachers.form("Edit Teacher", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[Teacher], routes.TeacherController.saveTeacher(id)))
  }

  def saveTeacher(id: String) = withUser { user => implicit request =>
    val (fullName, position, email, password) = teacherForm.bindFromRequest().get
    val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/", id)
    val teacher = User.find(id).get.asInstanceOf[Teacher]
    if(filename != ""){
      User.save(teacher.copy(fullName=fullName, position=position, email=email, password=password, avatar = filename))
    }else{
      User.save(teacher.copy(fullName=fullName, position=position, email=email, password=password))
    }
    Redirect(routes.TeacherController.teacherList())
  }

  def removeTeacher(id: String) = withUser { user => implicit request =>
    User.delete(id)
    Redirect(routes.TeacherController.teacherList())
  }
}
