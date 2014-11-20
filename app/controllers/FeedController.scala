package controllers

import play.api.mvc.{Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import model.users._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import model.{users, Feed}
import org.joda.time.DateTime
import model.users.Student
import model.users.DeanManager
import model.users.DepartmentManager

object FeedController extends Controller {

  val feedForm = Form{
      "text" -> text
  }

  def addFeed() = Action { implicit request =>
    val text = feedForm.bindFromRequest().get
    val user = users.User.findByEmail(session.get("username").get).get
    Feed.insert(Feed(author = user.id, text = text, date= DateTime.now()))
    user match {
      case _: DepartmentManager =>
        DepartmentController.changeView(views.html.profile.news.view())
      case _: DeanManager =>
        DeanController.changeView(views.html.profile.news.view())
      case _: Student =>
        StudentController.changeView(views.html.profile.news.view())
      case _: Teacher =>
        TeacherController.changeView(views.html.profile.news.view())
      case _ =>
        Ok(views.html.index.index())
    }
  }

  def openFeed() = Action { implicit request =>
    val user = users.User.findByEmail(session.get("username").get).get
    user match {
      case _: DepartmentManager =>
          DepartmentController.changeView(views.html.profile.news.view())
      case _: DeanManager =>
          DeanController.changeView(views.html.profile.news.view())
      case _: Student =>
          StudentController.changeView(views.html.profile.news.view())
      case _: Teacher =>
        TeacherController.changeView(views.html.profile.news.view())
      case _ =>
        Ok(views.html.index.index())
    }
  }

}
