package controllers.teacher

import model.users.Teacher
import controllers.UserController

trait TeacherController  extends UserController {

  val role: Array[String] = Array(Teacher.getClass.getName)

  layout = views.html.profile.teacher.teacherLayout
  profile = routes.ScheduleController.viewSchedule()
}
