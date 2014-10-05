package controllers

import play.api.mvc.{Session, SimpleResult, Action, Controller}
import play.api.templates._
import model.users.User

trait UserController extends Controller {

  var theme: String
  var layout: { def apply(content: Html, theme: Predef.String) : HtmlFormat.Appendable} = _

  var profile: { def apply()(implicit session: Session): HtmlFormat.Appendable } = _
  var lookAndFeelPath:  { def apply(): HtmlFormat.Appendable } = _
  var profileRedirect: SimpleResult =_

  def profileRedirectImpl = Action{implicit request => changeView(profile())}
  def openLookAndFeel = Action {changeView(lookAndFeelPath())}
  def changeView(view: Html) = Ok(layout(view, theme))
  def profileInit(id: String) = Action{implicit request =>
    val user = User.find(id).get
    theme =  user.theme
    changeView(profile())
  }
  def changeTheme(theme: String) = Action {implicit request =>
    val user = User.findByEmail(session.get("username").get).get
    user.theme = theme
    User.save(user)
    this.theme = theme
    profileRedirect
  }
}
