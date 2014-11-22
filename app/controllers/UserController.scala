package controllers

import play.api.mvc._
import play.api.templates._
import model.users.{User}
import play.api.Play.current
import se.radley.plugin.salat
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global


trait UserController extends Controller with Secured {

  var layout: {def apply(content: Html, user: User): HtmlFormat.Appendable} = _
  var profile: Call = _


  def changeView(view: Html)(implicit session: Session) = {
    val user = User.findByEmail(session.get("username").get).get
    Ok(layout(view, user))
  }

  def profileInit(id: String) = Action {
    implicit request =>
      Redirect(profile)
  }
}
