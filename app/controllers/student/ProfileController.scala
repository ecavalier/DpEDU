package controllers.student

import play.api.data.Form
import play.api.data.Forms._
import model.users.{Student, User}
import controllers.Application
import play.api.mvc.Security

object ProfileController extends StudentController {

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text,
      "phone" -> text
    )
  )

  val lookAndFeelPath =  views.html.profile.student.lookAndFeel
  def openLookAndFeel = withUser { user => implicit request =>
    changeView(lookAndFeelPath())
  }

  def updateProfile() = withUser { user => implicit request =>
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
      user => Redirect(controllers.routes.LoginController.reAuth()).withSession(Security.username -> user._1)
    )
  }
}
