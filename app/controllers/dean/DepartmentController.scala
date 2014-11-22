package controllers.dean

import model.Department
import model.users.{User}
import play.api.data.Form
import play.api.data.Forms._
import model.users.DepartmentManager


object DepartmentController extends DeanController {

  val departmentForm = Form(
    tuple(
      "name" -> text,
      "description" -> text
    )
  )

  val managerForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    )
  )

  def departmentList = withUser { user => implicit request =>
    changeView(views.html.profile.dean.department.list())
  }

  def deleteDepartment(id: String) = withUser { user => implicit request =>
      Department.delete(id)
      Redirect(routes.DepartmentController.departmentList())
  }

  def saveDepartment(id: String) = withUser { user => implicit request =>
      val (name, description) = departmentForm.bindFromRequest().get
      val dep = Department.find(id)
      Department.save(dep.get.copy(name = name, description = description))
      Redirect(routes.DepartmentController.departmentList())
  }

  def insertDepartment() = withUser { user => implicit request =>
      val (name, description) = departmentForm.bindFromRequest().get
      Department.create(name, description)
      Redirect(routes.DepartmentController.departmentList())
  }

  def detailsDepartment(id: String) = withUser { user => implicit request =>
      val department = Department.find(id)
      changeView(views.html.profile.dean.department.details(department.get))
  }

  def getDepartmentEditForm(id: String) = withUser { user => implicit request =>
    Ok(views.html.profile.dean.department.form("Edit Department", "editForm", "Update" ,Department.find(id).get,
      routes.DepartmentController.saveDepartment(id)))
  }

  def addManager(departmentId: String) = withUser { user => implicit request =>
      val (email, password) = managerForm.bindFromRequest().get
      val department = Department.find(departmentId).get
      val manager = new DepartmentManager(email = email, password = password, departmentId = department.id)
      User.insert(manager)
      Redirect(routes.DepartmentController.detailsDepartment(department.id.toString))
  }

  def saveManager(id: String) = withUser { user => implicit request =>
      val manager = User.find(id).get.asInstanceOf[DepartmentManager]
      val (email, password) = managerForm.bindFromRequest().get
      User.save(manager.copy(email=email, password=password))
      Redirect(routes.DepartmentController.detailsDepartment(manager.departmentId.toString))
  }

  def removeManager(id: String) = withUser { user => implicit request =>
      val manager = User.find(id).get.asInstanceOf[DepartmentManager]
      User.delete(id)
      Redirect(routes.DepartmentController.detailsDepartment(manager.departmentId.toString))
  }

  def getManagerEditForm(id: String) = withUser { user => implicit request =>
    Ok(views.html.profile.dean.department.managerForm("Edit Manager", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[DepartmentManager], routes.DepartmentController.saveManager(id)))
  }

}
