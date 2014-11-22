package controllers.dean

import play.api.data.Form
import play.api.data.Forms._
import model.users.{DeanManager, User}
import controllers.Application
import play.api.mvc.{Security}

object ProfileController extends DeanController {

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text
    )
  )

  val lookAndFeelPath = views.html.profile.dean.lookAndFeel
  def openLookAndFeel = withUser { user => implicit request =>
    changeView(lookAndFeelPath())
  }

  def updateProfile = withUser{ user => implicit request =>
    val (email, password, theme) = profileForm.bindFromRequest().get
    val u = User.findByEmail(session.get("username").get).get.asInstanceOf[DeanManager]
    val filename= Application.pictureUpload(request, "avatar", "/home/ivan/appTest/", u.id.toString)
    if(filename != ""){
      User.save(u.copy(email = email, password=password, theme=theme, avatar=filename))
    }else{
      User.save(u.copy(email = email, password=password, theme=theme))
    }
    profileForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index.index()),
      user => Redirect(controllers.routes.LoginController.login()).withSession(Security.username -> user._1)
    )
  }

}
