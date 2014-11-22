package controllers.department

import controllers._
import model.users.DepartmentManager

trait DepartmentController extends UserController {

  val role: Array[String] = Array(DepartmentManager.getClass.getName)

  layout = views.html.profile.department.departmentLayout
  profile = routes.GroupController.groupList()
}
