package controllers


import play.api.mvc.{Security, Action}
import play.api.data.Forms._
import play.api.data.Form
import model.Department
import model.users._
import model.users.DepartmentManager


object DeanController extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.dean.adminLayout
  profile = views.html.profile.dean.profile
  lookAndFeelPath = views.html.profile.dean.lookAndFeel
  profileRedirect = Redirect(routes.DeanController.profileRedirectImpl())

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

  val profileForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "theme" -> text
    )
  )

  def departmentList = Action {
    implicit request =>
    changeView(views.html.profile.dean.department.list())
  }

  def deleteDepartment(id: String) = Action {
    implicit request =>
    Department.delete(id)
    Redirect(routes.DeanController.departmentList())
  }

  def saveDepartment(id: String) = Action {
    implicit request =>
      val (name, description) = departmentForm.bindFromRequest().get
      val dep = Department.find(id)
      Department.save(dep.get.copy(name = name, description = description))
      Redirect(routes.DeanController.departmentList())
  }

  def getDepartmentEditForm(id: String) = Action {
    Ok(views.html.profile.dean.department.form("Edit Department", "editForm", "Update" ,Department.find(id).get,
      routes.DeanController.saveDepartment(id)))
  }

  def getManagerEditForm(id: String) = Action {
    Ok(views.html.profile.dean.department.managerForm("Edit Manager", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[DepartmentManager], routes.DeanController.saveManager(id)))
  }

  def insertDepartment() = Action {
    implicit request =>
      val (name, description) = departmentForm.bindFromRequest().get
      Department.create(name, description)
      Redirect(routes.DeanController.departmentList())
  }

  def detailsDepartment(id: String) = Action {
    implicit request =>
    val department = Department.find(id)
    changeView(views.html.profile.dean.department.details(department.get))
  }

  def addManager(departmentId: String) = Action {
    implicit request =>
      val (email, password) = managerForm.bindFromRequest().get
      val department = Department.find(departmentId).get
      val manager = new DepartmentManager(email = email, password = password, departmentId = department.id)
      User.insert(manager)
      Redirect(routes.DeanController.detailsDepartment(department.id.toString))
  }

  def saveManager(id: String) = Action {
    implicit request =>
      val manager = User.find(id).get.asInstanceOf[DepartmentManager]
      val (email, password) = managerForm.bindFromRequest().get
      User.save(manager.copy(email=email, password=password))
      Redirect(routes.DeanController.detailsDepartment(manager.departmentId.toString))
  }

  def removeManager(id: String) = Action{
    implicit request =>
    val manager = User.find(id).get.asInstanceOf[DepartmentManager]
    User.delete(id)
    Redirect(routes.DeanController.detailsDepartment(manager.departmentId.toString))
  }

  def updateProfile = Action(parse.multipartFormData) { implicit request =>
      val (email, password, theme) = profileForm.bindFromRequest().get
      val u = User.findByEmail(session.get("username").get).get.asInstanceOf[DeanManager]
      var filename=""
      request.body.file("avatar").map { picture =>
        import java.io.File
        filename = "/home/ivan/appTest/"+u.id.toString
        picture.ref.moveTo(new File(filename), true)
      }
      if(filename != ""){
        User.save(u.copy(email = email, password=password, theme=theme, avatar=filename))
      }else{
        User.save(u.copy(email = email, password=password, theme=theme))
      }
      profileForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.index.index()),
        user => Redirect(routes.LoginController.login()).withSession(Security.username -> user._1)
      )
  }}

