package controllers

import play.api.mvc.Action
import play.api.data.Forms._
import play.api.data.Form
import model.Department
import model.users._
import model.users.DepartmentManager

object Dean extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.dean.adminLayout
  profile = views.html.profile.dean.profile
  lookAndFeelPath = views.html.profile.dean.lookAndFeel
  profileRedirect =   Redirect(routes.Dean.profileRedirectImpl())

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
    val manager = new DepartmentManager(email = email, password = password, departmentId=department.id.toString)
    User.insert(manager)
    Department.save(department.copy(managers = department.managers.++: (List(manager.id.toString))))
    Redirect(routes.Dean.detailsDepartment(department.id.toString))
  }
}
