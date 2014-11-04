package controllers

import play.api.mvc._
import play.api.templates._
import model.users.{User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.SimpleResult


trait UserController extends Controller {

  var theme: String
  var layout: {def apply(content: Html, user: User): HtmlFormat.Appendable} = _

  var profile: {def apply()(implicit session: Session): HtmlFormat.Appendable} = _
  var lookAndFeelPath: {def apply()(implicit session: Session, req: RequestHeader): HtmlFormat.Appendable} = _
  var profileRedirect: SimpleResult = _

  def profileRedirectImpl = Action {
    implicit request => changeView(profile())
  }

  def openLookAndFeel = Action {
    implicit request => changeView(lookAndFeelPath())
  }


  def changeView(view: Html)(implicit session: Session) = {
    val user = User.findByEmail(session.get("username").get).get
    Ok(layout(view, user))
  }

  def profileInit(id: String) = Action {
    implicit request =>
      val user = User.find(id).get
      theme = user.theme
      changeView(profile())
  }

  def changeTheme(theme: String) = Action {
    implicit request =>
      val user = User.findByEmail(session.get("username").get).get
      user.theme = theme
      User.save(user)
      this.theme = theme
      profileRedirect
  }

  def picture(name: String) = Action {
    Ok.sendFile(new java.io.File(name))
  }
}
