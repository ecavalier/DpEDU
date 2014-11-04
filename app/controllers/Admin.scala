package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._


object Admin extends Controller {

  val departmentForm = Form(
    "name" -> text
  )


}
