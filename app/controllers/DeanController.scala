package controllers


import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import model.{RoomType, Room, Department}
import model.users._
import model.users.DepartmentManager
import model.users.DepartmentManager
import model.users.DeanManager
import play.api.libs.Files


object DeanController extends UserController {

  var theme = "bootstrap.min.css"
  layout = views.html.profile.dean.adminLayout
  profile = views.html.profile.dean.profile
  lookAndFeelPath = views.html.profile.dean.lookAndFeel
  profileRedirect = Redirect(routes.DeanController.profileRedirectImpl())

  // Forms Section
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

  val roomForm = Form(
    tuple(
      "name" -> text,
      "roomType" -> text
    )
  )

  //Department Section
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

  def getDepartmentEditForm(id: String) = Action {
    Ok(views.html.profile.dean.department.form("Edit Department", "editForm", "Update" ,Department.find(id).get,
      routes.DeanController.saveDepartment(id)))
  }


  //Manager Section
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

  def getManagerEditForm(id: String) = Action {
    Ok(views.html.profile.dean.department.managerForm("Edit Manager", "editForm", "Update" ,
      User.find(id).get.asInstanceOf[DepartmentManager], routes.DeanController.saveManager(id)))
  }

  //Rooms Section
  def roomsList() = Action {
    implicit request =>
      changeView(views.html.profile.dean.rooms.list())
  }

  def addRoom() = Action(parse.multipartFormData) {
    implicit request =>
      val (name, roomType) = roomForm.bindFromRequest().get
      val filename= pictureUpload(request, "picture", "/home/ivan/appTest/room/", name)
      if(filename != ""){
        Room.insert(new Room(name=name, roomType = roomType, picture = filename))
      }else{
        Room.insert(new Room(name=name, roomType = roomType))
      }
      Redirect(routes.DeanController.roomsList())
  }

  def getRoomEditForm(id: String) = Action {
    Ok(views.html.profile.dean.rooms.form("Edit Room", "editForm", "Update" ,
      Room.find(id).get, routes.DeanController.saveRoom(id)))
  }

  def saveRoom(id: String) =  Action(parse.multipartFormData) {
    implicit request =>
      val room = Room.find(id).get
      val (name, roomType) = roomForm.bindFromRequest().get
      val filename= pictureUpload(request, "picture", "/home/ivan/appTest/room/", name)
      if(filename != ""){
        Room.save(room.copy(name=name, roomType=roomType, picture=filename))
      }else{
        Room.save(room.copy(name=name, roomType=roomType))
      }
      changeView(views.html.profile.dean.rooms.list())
  }

  def removeRoom(id: String) = Action{
    implicit request =>
      Room.delete(id)
      Redirect(routes.DeanController.roomsList())
  }

  def updateProfile() = Action(parse.multipartFormData) { implicit request =>
      val (email, password, theme) = profileForm.bindFromRequest().get
      val u = User.findByEmail(session.get("username").get).get.asInstanceOf[DeanManager]
      val filename= pictureUpload(request, "avatar", "/home/ivan/appTest/", u.id.toString)
      if(filename != ""){
        User.save(u.copy(email = email, password=password, theme=theme, avatar=filename))
      }else{
        User.save(u.copy(email = email, password=password, theme=theme))
      }
      profileForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.index.index()),
        user => Redirect(routes.LoginController.login()).withSession(Security.username -> user._1)
      )
  }

  def pictureUpload(req: Request[MultipartFormData[Files.TemporaryFile]], strMap: String, dirPath: String,
                    newFileName: String): String  = {
      var filename = ""
      req.body.file(strMap).map { picture =>
      import java.io.File
      filename = dirPath+newFileName
      picture.ref.moveTo(new File(filename), replace = true)
    }
    filename
  }

}

