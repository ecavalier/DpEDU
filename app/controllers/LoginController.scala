package controllers

import play.api.data._
import play.api.mvc._
import play.api.data.Forms._
import model.users._
import model.users.Student
import model.users.DepartmentManager
import model.users.DeanManager

object LoginController extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    !User.findByEmail(username).isEmpty
  }

  def login = Action {
    implicit request =>
      val user = User.findByEmail(session.get("username").get).get
      user match {
        case _: DepartmentManager =>
          Redirect(routes.DepartmentController.profileInit(user.id.toString))
        case _: DeanManager =>
          Redirect(routes.DeanController.profileInit(user.id.toString))
        case _: Student =>
          Redirect(routes.StudentController.profileInit(user.id.toString))
        case _: Teacher =>
          Redirect(routes.DeanController.profileInit(user.id.toString))
        case _ =>
          Ok(views.html.index.index())
      }

  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.index.index()),
        user => Redirect(routes.LoginController.login()).withSession(Security.username -> user._1)
      )
  }

  def logOut = Action {
    implicit request =>
      session.+("username", "")
      Ok(views.html.index.index())
  }

}
