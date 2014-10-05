package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Forms._
import play.api.data.Form
import play.api.templates.Html
import model.Department
import model.users._
import model.users.DepartmentManager
import model.users.DepartmentManager
import play.mvc.Http

object Dean extends Controller {
  val departmentForm = Form(
    tuple(
      "name" -> text,
      "description" -> text
    )
  )

  val managerForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    )
  )
   var theme = "bootstrap.min.css"
  def profileInit(id: String) = Action{implicit request =>
    val user = User.find(id).get.asInstanceOf[DeanManager]
    theme = user.theme
    changeView(views.html.profile.dean.profile())
  }

  def departmentList = Action {
    changeView(views.html.profile.dean.department.list())
  }

  def deleteDepartment(id: String) = Action {
    Department.delete(id)
    Redirect(routes.Dean.departmentList())
  }


  def insertDepartment() = Action {implicit request =>
    val (name, description) = departmentForm.bindFromRequest().get
    Department.create(name, description)
    Redirect(routes.Dean.departmentList())
  }

  def detailsDepartment(id: String) = Action {
    val department = Department.find(id)
    changeView(views.html.profile.dean.department.details(department.get))
  }

  def addManager(departmentId: String) =   Action {implicit request =>
    val (email, password) = managerForm.bindFromRequest().get
    val department = Department.find(departmentId).get
    val manager = new DepartmentManager(email = email, password = password)
    User.insert(manager)
    Department.save(department.copy(managers = department.managers.++: (List(manager.id.toString))))
    Redirect(routes.Dean.detailsDepartment(department.id.toString))
  }

  def changeTheme(theme: String) = Action {implicit request =>
    val user = User.findByEmail(session.get("username").get).get
    user.asInstanceOf[DeanManager].theme = theme
    User.save(user)
    this.theme = theme
    Redirect(routes.Dean.departmentList())
  }

  def openLookAndFeel = Action {
    changeView(views.html.profile.dean.lookAndFeel())
  }

  def changeView(view: Html) = Ok(views.html.profile.dean.adminLayout(view, theme))


}
