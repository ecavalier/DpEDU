package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import model._

object DepartmentController extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.department.departmentLayout
  profile = views.html.profile.department.profile
  lookAndFeelPath =  views.html.profile.dean.lookAndFeel
  profileRedirect = Redirect(routes.DepartmentController.profileRedirectImpl())


  val groupForm = Form(
    "name" -> text
  )

  def groupList = Action {
    implicit request =>
      changeView(views.html.profile.department.groups.list())
  }

  def insertDepartment() = Action {
    implicit request =>
      val name = groupForm.bindFromRequest().get
      Group.create(name,
        users.User.findByEmail(session.get("username").get).get.asInstanceOf[users.DepartmentManager].departmentId)
      Redirect(routes.DepartmentController.groupList())
  }


}
