package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Security, Action}
import model._
import model.users._
import model.users.DepartmentManager
import model.users.Teacher
import com.mongodb.casbah.commons.TypeImports.ObjectId

object DepartmentController extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.department.departmentLayout
  profile = views.html.profile.department.groups.list
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

  val studentForm = Form(
    tuple(
      "fullName" -> text,
      "email" -> text,
      "password" -> text,
      "phone"-> text,
      "isElder"-> boolean
    )
  )

  val subjectForm = Form(
    tuple(
      "name" -> text,
      "subjectType" -> list(text),
      "teacher" -> text
    )
  )


  //Teacher Section
  def teacherList() = Action { implicit request =>
    changeView(views.html.profile.department.teachers.list(User.findByEmail(session.get("username").get).get
      .asInstanceOf[DepartmentManager].departmentId.toString))
  }

  def addTeacher() = Action(parse.multipartFormData) { implicit request =>
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

  def saveTeacher(id: String) = Action(parse.multipartFormData) { implicit request =>
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
  def groupList() = Action { implicit request =>
    changeView(views.html.profile.department.groups.list())
  }

  def  addGroup() = Action { implicit request =>
    val (name, curator) = groupForm.bindFromRequest().get
    val departmentId =  users.User.findByEmail(session.get("username").get).get.asInstanceOf[users.DepartmentManager].departmentId
    Group.insert(Group(name = name, curator=new ObjectId(curator), departmentId=departmentId, elder = null))
    Redirect(routes.DepartmentController.groupList())
  }

  def removeGroup(id: String) = Action {
    Group.delete(id)
    Redirect(routes.DepartmentController.groupList())
  }

  def groupDetails(id: String) = Action { implicit request =>
    changeView(views.html.profile.department.groups.details(Group.find(id).get))
  }

  def saveGroup(id: String) = Action { implicit request =>
    val (name, curator) = groupForm.bindFromRequest().get
    val group = Group.find(id).get
    Group.save(group.copy(name = name, curator = new ObjectId(curator)))
    Redirect(routes.DepartmentController.groupDetails(group.id.toString))
  }

  //Student
  def addStudent(id: String) = Action(parse.multipartFormData) { implicit request =>
    val (fullName, email, password, phone, isElder) = studentForm.bindFromRequest().get
    val studentId = User.insert(Student(fullName=fullName, email=email, password=password, phone = phone,
      group = new ObjectId(id)))
    if(isElder){
      val group = Group.find(id).get
      Group.save(group.copy(elder = studentId.get))
    }
    val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/", studentId.get.toString)
    if(filename != ""){
      val student = users.User.findByEmail(email).get.asInstanceOf[users.Student]
      User.save(student.copy(avatar = filename))
    }
    Redirect(routes.DepartmentController.groupDetails(id))
  }

  def removeStudent(id: String, groupId: String) = Action{
    val group =  Group.find(groupId).get
    if(group.elder!=null && group.elder.toString == id){ Group.save(group.copy(elder=null)) }
    User.delete(id)
    Redirect(routes.DepartmentController.groupDetails(groupId))
  }

  def getStudentEditForm(id: String, groupId: String) = Action { implicit request =>
      Ok(views.html.profile.department.groups.studentForm("Edit Student", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[Student], routes.DepartmentController.saveStudent(id, groupId)))
  }

  def saveStudent(id: String, groupId: String) = Action(parse.multipartFormData) { implicit request =>
    val (fullName, email, password, phone, isElder) = studentForm.bindFromRequest().get
    val student = User.find(id).get.asInstanceOf[Student]
    if(isElder){
      val group = Group.find(groupId).get
      Group.save(group.copy(elder = student.id))
    }
    val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/", student.id.toString)
    if(filename != ""){
      User.save(student.copy(fullName=fullName, email=email, password=password, phone = phone, avatar = filename))
    }else{
      User.save(student.copy(fullName=fullName, email=email, password=password, phone = phone))
    }
    Redirect(routes.DepartmentController.groupDetails(groupId))
  }

  //Study Plan Section
  def planGroupList() = Action { implicit request =>
    changeView(views.html.profile.department.plans.groups())
  }

  def planList(id: String) = Action { implicit request =>
    changeView(views.html.profile.department.plans.list(Group.find(id).get))
  }

  def addSubject(id: String) = Action { implicit request =>
    val (name, subjectType, teacher) = subjectForm.bindFromRequest().get
    Subject.insert(Subject(name = name, subjectType = subjectType, teacher = new ObjectId(teacher),
    group = new ObjectId(id)))
    Redirect(routes.DepartmentController.planList(id))
  }

  def removeSubject(id: String, departmentId: String) = Action {
    Subject.delete(id)
    Redirect(routes.DepartmentController.planList(departmentId))
  }

  def getSubjectEditForm(id: String) = Action { implicit request =>
    val subject = Subject.find(id).get
    Ok(views.html.profile.department.plans.form("Edit Subject", "editForm", "Update" ,
      subject, routes.DepartmentController.saveSubject(id)))
  }

  def saveSubject(id: String) = Action { implicit request =>
    val subject = Subject.find(id).get
    val (name, subjectType, teacher) = subjectForm.bindFromRequest().get
    Subject.save(subject.copy(name=name, subjectType=subjectType, teacher=new ObjectId(teacher)))
    Redirect(routes.DepartmentController.planList(subject.group.toString))
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
