package controllers.dean

import controllers.{Secured, UserController}
import model.users.{User, DeanManager}


trait DeanController extends UserController with Secured {

  val role: Array[String] = Array(DeanManager.getClass.getName)

  layout = views.html.profile.dean.adminLayout
  profile = routes.StudentSearchController.searchStudent()
}

