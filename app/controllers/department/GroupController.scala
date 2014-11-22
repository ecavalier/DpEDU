package controllers.department

import model.{Group, users}
import org.bson.types.ObjectId
import play.api.data.Form
import play.api.data.Forms._

object GroupController extends DepartmentController {

  val groupForm = Form(
    tuple(
      "name" -> text,
      "curator" -> text
    )
  )

  def groupList() = withUser { user => implicit request =>
    changeView(views.html.profile.department.groups.list())
  }

  def  addGroup() = withUser { user => implicit request =>
    val (name, curator) = groupForm.bindFromRequest().get
    val departmentId =  users.User.findByEmail(session.get("username").get).get.asInstanceOf[users.DepartmentManager].departmentId
    Group.insert(Group(name = name, curator=new ObjectId(curator), departmentId=departmentId, elder = null))
    Redirect(routes.GroupController.groupList())
  }

  def removeGroup(id: String) = withUser { user => implicit request =>
    Group.delete(id)
    Redirect(routes.GroupController.groupList())
  }

  def groupDetails(id: String) = withUser { user => implicit request =>
    changeView(views.html.profile.department.groups.details(Group.find(id).get))
  }

  def saveGroup(id: String) = withUser { user => implicit request =>
    val (name, curator) = groupForm.bindFromRequest().get
    val group = Group.find(id).get
    Group.save(group.copy(name = name, curator = new ObjectId(curator)))
    Redirect(routes.GroupController.groupDetails(group.id.toString))
  }
}
