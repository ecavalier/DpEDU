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
    if(!User.findByEmail(username).isEmpty){
      currentUser = User.findByEmail(username).get
      true
    }else{
      false
    }

  }

  var currentUser: User = _

  def login = Action {
    implicit request =>
      if(!loginForm.bindFromRequest().hasErrors){
        val (username, password) = loginForm.bindFromRequest().get
        currentUser = User.findByEmail(username).get
        if(password == currentUser.password){
          auth
        }else{
          Redirect(routes.Application.index()).withNewSession
        }
      }else{
        Redirect(routes.Application.index()).withNewSession
      }
  }

  def reAuth = Action {
    implicit request =>
      currentUser = User.findByEmail(session.get("username").get).get
      auth
  }

  def auth = {
    currentUser match {
      case _: DepartmentManager =>
        Redirect(department.routes.ProfileController.profileInit(currentUser.id.toString)).withSession(Security
          .username -> currentUser.email)
      case _: DeanManager =>
        Redirect(dean.routes.ProfileController.profileInit(currentUser.id.toString)).withSession(Security.username ->
          currentUser.email)
      case _: Student =>
        Redirect(student.routes.ProfileController.profileInit(currentUser.id.toString)).withSession(Security.username -> currentUser
          .email)
      case _: Teacher =>
        Redirect(teacher.routes.ProfileController.profileInit(currentUser.id.toString)).withSession(Security.username -> currentUser
          .email)
      case _ =>
        Ok(views.html.accessFailed())
    }
  }

  def logOut = Action { implicit request =>
      Redirect(routes.Application.index()).withNewSession
  }

}
