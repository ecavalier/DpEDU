package controllers

import play.api.mvc._
import model._
import play.api.data._
import play.api.data.Forms._
import model.Department

object Admin extends Controller {

  val departmentForm = Form(
      "name" -> text
  )


}
