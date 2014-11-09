package controllers

import play.api.mvc.{Security, Action}
import model.users.{DeanManager, Student, User}
import play.api.data.Form
import play.api.data.Forms._

object StudentController extends UserController {

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text,
      "phone" -> text
    )
  )

  layout = views.html.profile.student.studentLayout
  profile = routes.StudentController.viewSchedule()
  lookAndFeelPath =  views.html.profile.student.lookAndFeel

  def viewSchedule() = Action { implicit request =>
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Student]
    changeView(views.html.profile.student.schedule.view(user.group.toString, "0"))
  }

  def updateProfile() = Action(parse.multipartFormData) { implicit request =>
    val (email, password, theme, phone) = profileForm.bindFromRequest().get
    val u = User.findByEmail(session.get("username").get).get.asInstanceOf[Student]
    val filename= Application.pictureUpload(request, "avatar", "/home/ivan/appTest/", u.id.toString)
    if(filename != ""){
      User.save(u.copy(email = email, password=password, phone=phone, theme=theme, avatar=filename))
    }else{
      User.save(u.copy(email = email, password=password, phone=phone, theme=theme))
    }
    profileForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index.index()),
      user => Redirect(routes.LoginController.login()).withSession(Security.username -> user._1)
    )
  }

}
