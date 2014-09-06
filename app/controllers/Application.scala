package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import model.UserData

object Application extends Controller {

  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText()
    )(UserData.apply)(UserData.unapply)
  )

  def index = Action {
    Ok(views.html.index.index(loginForm = loginForm))
  }

}