package controllers


import play.api.data._
import play.api.mvc._
import play.api.data.Forms._
import model.users._
import model.users.Student
import model.users.DepartmentManager
import model.users.DeanManager


object Login extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def check(username: String, password: String) = {
    !User.findByEmail(username).isEmpty
  }

  def login = Action { implicit request =>
    val user = User.findByEmail(session.get("username").get).get
    user match {
      case _: DepartmentManager =>
        Ok(views.html.profile.department.departmentLayout(views.html.profile.department.profile()))
      case _: DeanManager =>
        Redirect(routes.Dean.profileInit(user.id.toString))
      case _: Student =>
        Ok(views.html.profile.student.studentLayout(views.html.profile.student.profile()))
      case _: Teacher =>
        Ok(views.html.profile.teacher.teacherLayout(views.html.profile.teacher.profile()))
      case _ =>
        Ok(views.html.index.index())
    }

  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index.index()),
      user => Redirect(routes.Login.login()).withSession(Security.username -> user._1)
    )
  }

  def logout = Action { Redirect(routes.Login.login) }

}
