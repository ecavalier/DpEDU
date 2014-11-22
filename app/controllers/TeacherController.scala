package controllers

import play.api.data.Form
import play.api.data.Forms._
import model.users.{Teacher, Student, User}
import play.api.mvc.{Action, Security}
import org.bson.types.ObjectId

object TeacherController  extends UserController {

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text
    )
  )

  layout = views.html.profile.teacher.teacherLayout
  profile = routes.TeacherController.viewSchedule()
  val lookAndFeelPath =  views.html.profile.teacher.lookAndFeel
  def openLookAndFeel = Action {
    implicit request => changeView(lookAndFeelPath())
  }

  def viewSchedule() = Action { implicit request =>
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Teacher]
    changeView(views.html.profile.teacher.shedule.view(user.id.toString, "0"))
  }

  def getShowTable() = Action { implicit request =>
    val week = request.getQueryString("week").get
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Teacher]
    Ok(views.html.profile.teacher.shedule.schedule(user.id, week.toInt))
  }


  def updateProfile() = Action{ implicit request =>
    val (email, password, theme) = profileForm.bindFromRequest().get
    val u = User.findByEmail(session.get("username").get).get.asInstanceOf[Teacher]
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

  val role: Array[String] = Array()
}
