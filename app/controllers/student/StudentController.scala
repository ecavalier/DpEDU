package controllers.student

import model.users.Student
import controllers.UserController

trait StudentController extends UserController {

  val role: Array[String] = Array(Student.getClass.getName)

  layout = views.html.profile.student.studentLayout
  profile = routes.ScheduleController.viewSchedule()
}
