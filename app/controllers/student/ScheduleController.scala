package controllers.student

import model.users.{Student, User}

object ScheduleController extends StudentController {

  def viewSchedule() = withUser { user => implicit request =>
    val user = User.findByEmail(session.get("username").get).get.asInstanceOf[Student]
    changeView(views.html.profile.student.schedule.view(user.group.toString, "0"))
  }
}
