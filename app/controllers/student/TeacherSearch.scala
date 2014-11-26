package controllers.student

import model.{TeacherFilter, ScheduleItem}
import org.bson.types.ObjectId

object TeacherSearch extends StudentController {

  def viewTeachers() = withUser { user => implicit request =>
    changeView(views.html.profile.student.teacher.teacherSearch())
  }

  def searchTeacher() = withUser { user => implicit request =>
    println("hello")
    val filter = TeacherFilter(teacherId = new ObjectId(request.getQueryString("teacher").get),
    day = request.getQueryString("day").get.toInt, week = request.getQueryString("week").get.toInt)
    Ok(views.html.profile.student.teacher.tableView(ScheduleItem.filterTeacher(filter)))
  }
}
