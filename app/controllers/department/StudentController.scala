package controllers.department

import model.users.User
import org.bson.types.ObjectId
import model.{users, Group}
import controllers.Application
import play.api.data.Form
import play.api.data.Forms._
import model.users.Student

object StudentController extends DepartmentController{

  val studentForm = Form(
    tuple(
      "fullName" -> text,
      "email" -> text,
      "password" -> text,
      "phone"-> text,
      "isElder"-> boolean
    )
  )

  def addStudent(id: String) = withUser { user => implicit request =>
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
    Redirect(routes.GroupController.groupDetails(id))
  }

  def removeStudent(id: String, groupId: String) = withUser { user => implicit request =>
    val group =  Group.find(groupId).get
    if(group.elder!=null && group.elder.toString == id){ Group.save(group.copy(elder=null)) }
    User.delete(id)
    Redirect(routes.GroupController.groupDetails(groupId))
  }

  def getStudentEditForm(id: String, groupId: String) = withUser { user => implicit request =>
    Ok(views.html.profile.department.groups.studentForm("Edit Student", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[Student], routes.StudentController.saveStudent(id, groupId)))
  }

  def saveStudent(id: String, groupId: String) = withUser { user => implicit request =>
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
    Redirect(routes.GroupController.groupDetails(groupId))
  }

}
