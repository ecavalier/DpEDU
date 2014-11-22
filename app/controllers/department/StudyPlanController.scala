package controllers.department

import model.{Subject, Group}
import org.bson.types.ObjectId
import play.api.data.Form
import play.api.data.Forms._

object StudyPlanController extends DepartmentController{
  val subjectForm = Form(
    tuple(
      "name" -> text,
      "subjectType" -> list(text),
      "teacher" -> text
    )
  )

  def planGroupList() = withUser { user => implicit request =>
    changeView(views.html.profile.department.plans.groups())
  }

  def planList(id: String) = withUser { user => implicit request =>
    changeView(views.html.profile.department.plans.list(Group.find(id).get))
  }

  def addSubject(id: String) = withUser { user => implicit request =>
    val (name, subjectType, teacher) = subjectForm.bindFromRequest().get
    Subject.insert(Subject(name = name, subjectType = subjectType, teacher = new ObjectId(teacher),
      group = new ObjectId(id)))
    Redirect(routes.StudyPlanController.planList(id))
  }

  def removeSubject(id: String, departmentId: String) = withUser { user => implicit request =>
    Subject.delete(id)
    Redirect(routes.StudyPlanController.planList(departmentId))
  }

  def getSubjectEditForm(id: String) = withUser { user => implicit request =>
    val subject = Subject.find(id).get
    Ok(views.html.profile.department.plans.form("Edit Subject", "editForm", "Update" ,
      subject, routes.StudyPlanController.saveSubject(id)))
  }

  def saveSubject(id: String) =  withUser { user => implicit request =>
    val subject = Subject.find(id).get
    val (name, subjectType, teacher) = subjectForm.bindFromRequest().get
    Subject.save(subject.copy(name=name, subjectType=subjectType, teacher=new ObjectId(teacher)))
    Redirect(routes.StudyPlanController.planList(subject.group.toString))
  }
}
