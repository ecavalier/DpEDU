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


trait UserController extends Controller {

  var layout: {def apply(content: Html, user: User): HtmlFormat.Appendable} = _
  var profile: Call = _
  var lookAndFeelPath: {def apply()(implicit session: Session, req: RequestHeader): HtmlFormat.Appendable} = _

  def openLookAndFeel = Action {
    implicit request => changeView(lookAndFeelPath())
  }

  def changeView(view: Html)(implicit session: Session) = {
    val user = User.findByEmail(session.get("username").get).get
    Ok(layout(view, user))
  }

  def profileInit(id: String) = Action {
    implicit request =>
      Redirect(profile)
  }


  def picture(name: String) = Action {

    import com.mongodb.casbah.Implicits._

    val gridFs = salat.gridFS("photos")

    gridFs.findOne(Map("filename" -> name)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
        )),
        Enumerator.fromStream(f.inputStream)
      )

      case None => NotFound
    }
  }
}
