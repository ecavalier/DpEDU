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
    currentUser = User.findByEmail(username).get
    true
  }

  var currentUser: User = _

  def login = Action {
    implicit request =>
      if(session.get("username").isEmpty){
        val (username, password) = loginForm.bindFromRequest().get
        currentUser = User.findByEmail(username).get
      }else{
        currentUser = User.findByEmail(session.get("username").get).get
      }
      currentUser match {
        case _: DepartmentManager =>
      Redirect(routes.DepartmentController.profileInit(currentUser.id.toString)).withSession(Security.username -> currentUser.email)
        case _: DeanManager =>
          Redirect(dean.routes.ProfileController.profileInit(currentUser.id.toString)).withSession(Security.username ->
            currentUser.email)
        case _: Student =>
          Redirect(routes.StudentController.profileInit(currentUser.id.toString)).withSession(Security.username -> currentUser.email)
        case _: Teacher =>
          Redirect(routes.TeacherController.profileInit(currentUser.id.toString)).withSession(Security.username -> currentUser.email)
        case _ =>
          Ok(views.html.accessFailed())
      }
  }

  def logOut = Action { implicit request =>
      Redirect(routes.Application.index()).withNewSession
  }

}
