package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Security, Action}
import model._
import model.users.{Teacher, DepartmentManager, DeanManager, User}
import com.mongodb.casbah.commons.TypeImports.ObjectId

object DepartmentController extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.department.departmentLayout
  profile = views.html.profile.department.profile
  lookAndFeelPath =  views.html.profile.department.lookAndFeel
  profileRedirect = Redirect(routes.DepartmentController.profileRedirectImpl())

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text
    )
  )

  val teacherForm = Form(
    tuple(
      "fullName" -> text,
      "position" -> text,
      "email" -> text,
      "password" -> text
    )
  )

  val groupForm = Form(
    tuple(
      "name" -> text,
      "curator" -> text
    )
  )



  //Teacher Section
  def teacherList() = Action {
    implicit request =>
      changeView(views.html.profile.department.teachers.list(User.findByEmail(session.get("username").get).get
        .asInstanceOf[DepartmentManager].departmentId.toString))
  }

  def addTeacher() = Action(parse.multipartFormData) {
    implicit request =>
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
      Redirect(routes.DepartmentController.teacherList())
  }

  def getTeacherEditForm(id: String) = Action {
    Ok(views.html.profile.department.teachers.form("Edit Teacher", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[Teacher], routes.DepartmentController.saveTeacher(id)))
  }

  def saveTeacher(id: String) = Action(parse.multipartFormData) {
    implicit request =>
      val (fullName, position, email, password) = teacherForm.bindFromRequest().get
      val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/", id)
      val teacher = User.find(id).get.asInstanceOf[Teacher]
      if(filename != ""){
        User.save(teacher.copy(fullName=fullName, position=position, email=email, password=password, avatar = filename))
      }else{
        User.save(teacher.copy(fullName=fullName, position=position, email=email, password=password))
      }
      Redirect(routes.DepartmentController.teacherList())
  }

  def removeTeacher(id: String) = Action{
    User.delete(id)
    Redirect(routes.DepartmentController.teacherList())
  }

  //Group Section
  def groupList() = Action {
    implicit request =>
      changeView(views.html.profile.department.groups.list())
  }

  def  addGroup() = Action {
    implicit request =>
      val (name, curator) = groupForm.bindFromRequest().get
      val departmentId =  users.User.findByEmail(session.get("username").get).get.asInstanceOf[users.DepartmentManager].departmentId
      Group.insert(Group(name = name, curator=new ObjectId(curator), departmentId=departmentId, elder = null))
      Redirect(routes.DepartmentController.groupList())
  }

  def removeGroup(id: String) = Action {
    Group.delete(id)
    Redirect(routes.DepartmentController.groupList())
  }


  //Profile Section
  def updateProfile() = Action(parse.multipartFormData) { implicit request =>
    val (email, password, theme) = profileForm.bindFromRequest().get
    val u = User.findByEmail(session.get("username").get).get.asInstanceOf[DepartmentManager]
    val filename= Application.pictureUpload(request, "avatar", "/home/ivan/appTest/", u.id.toString)
    if(filename != ""){
      User.save(u.copy(email = email, password=password, theme=theme, avatar=filename))
    }else{
      User.save(u.copy(email = email, password=password, theme=theme))
    }
    profileForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index.index()),
      user => Redirect(routes.LoginController.login()).withSession(Security.username -> user._1)
    )
  }


}
