package controllers

import play.api.mvc._
import model.users._
import model.users.Teacher
import model.users.Student
import model.users.DeanManager

object Application extends Controller {


  def index = Action {
   User.insert(new Student(email="student", password="1"))
    User.insert(new Teacher(email="teacher", password="1"))
    User.insert(new DeanManager(email="dean", password="1"))
    User.insert(new DepartmentManager(email="department", password="1"))

    Ok(views.html.index.index())
  }

}
