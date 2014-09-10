package controllers

import play.api.mvc._
import model._
import play.api.data._
import play.api.data.Forms._
import model.Department




/**
 * Created by Ivan Yatcuba on 9/6/14.
 */

object Admin extends Controller {



  val facultyForm = Form(
    tuple(
      "name" -> text,
      "description" -> text
    )
  )

  val departmentForm = Form(
      "name" -> text
  )

  val groupForm = Form(
    tuple(
      "name" -> text,
      "departmentId" -> text
    )
  )


  def faculty = Action {
    val f: List[Faculty] = Faculty.findAll().toList
    Ok(views.html.profile.admin.adminLayout(views.html.profile.admin.faculty.list(f)))
  }

  def insertFaculty()  = Action {implicit request =>
    val (name, description) = facultyForm.bindFromRequest().get
    Faculty.create(name, description)
    Redirect(routes.Admin.faculty())
  }

  def deleteFaculty(id: String)  = Action {
    Faculty.delete(id)
    Redirect(routes.Admin.faculty())
  }

  def showFacultyDepartments(id: String)  = Action {
    val f = Faculty.find(id)
    Ok(views.html.profile.admin.adminLayout(
       views.html.profile.admin.faculty.details(
       f.get, views.html.profile.admin.faculty.departments(id))))
  }

  def showFacultyGroups(id: String)  = Action {
    Ok(views.html.profile.admin.adminLayout(
      views.html.profile.admin.faculty.details(
        Faculty.find(id).get, views.html.profile.admin.faculty.groups(id))))
  }

  def insertDepartment(id: String) = Action {implicit request =>
    Department.create(departmentForm.bindFromRequest().get, id)
    Redirect(routes.Admin.showFacultyDepartments(id))
  }

  def deleteDepartment(id: String, facultyId: String)  = Action {
    Department.delete(id)
    Redirect(routes.Admin.showFacultyDepartments(facultyId))
  }

  def insertGroup(id: String) = Action {implicit request =>
    val (name, departmentId) = groupForm.bindFromRequest().get
    Group.create(name, departmentId)
    Redirect(routes.Admin.showFacultyGroups(id))
  }

  def deleteGroup(id: String, departmentId: String)  = Action {
    Group.delete(id)
    Redirect(routes.Admin.showFacultyGroups(Department.find(departmentId).get.facultyId.toString))
  }
}
