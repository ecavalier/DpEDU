package controllers

import play.api.mvc.{Action, Controller}
import model.{Department, Group}
import play.api.data.Form
import play.api.data.Forms._

object DepartmentController extends Controller{

  val groupForm = Form(
    tuple(
      "name" -> text,
      "departmentId" -> text
    )
  )


}
