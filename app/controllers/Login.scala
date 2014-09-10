package controllers


import play.api.data._
import play.api.mvc._
import model.{Department, User, Admin}
import play.api.data.Forms._


/**
 * Created by Ivan Yatcuba on 9/6/14.
 */
object Login extends Controller {

  var currentUser: Admin = _

  val loginForm = Form{
    tuple(
      "email" -> text,
      "password" -> text
    )
  }

  def login = Action {implicit request =>
    val (email, pass) = loginForm.bindFromRequest().get
    val u = new Admin(email = email,password = pass, fullName = "None")
    currentUser = u
    Ok(views.html.profile.admin.adminLayout(views.html.profile.admin.profile(u)))
  }

  def openProfile = Action {
    Ok(views.html.profile.admin.adminLayout(views.html.profile.admin.profile(currentUser)))
  }

}
