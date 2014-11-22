package controllers.dean

import model.Room
import play.api.data.Form
import play.api.data.Forms._
import controllers.Application

object RoomsController extends DeanController {


  val roomForm = Form(
    tuple(
      "name" -> text,
      "roomType" -> text
    )
  )

  def roomsList() = withUser { user => implicit request =>
      changeView(views.html.profile.dean.rooms.list())
  }

  def addRoom() =  withUser{ user => implicit request =>
      val (name, roomType) = roomForm.bindFromRequest().get
      val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/room/",
        name)
      if(filename != ""){
        Room.insert(new Room(name=name, roomType = roomType, picture = filename))
      }else{
        Room.insert(new Room(name=name, roomType = roomType))
      }
      Redirect(routes.RoomsController.roomsList())
  }

  def getRoomEditForm(id: String) =  withUser{ user => implicit request =>
    Ok(views.html.profile.dean.rooms.form("Edit Room", "editForm", "Update" ,
      Room.find(id).get, routes.RoomsController.saveRoom(id)))
  }

  def saveRoom(id: String) =  withUser { user => implicit request =>
      val room = Room.find(id).get
      val (name, roomType) = roomForm.bindFromRequest().get
      val filename= Application.pictureUpload(request, "picture", "/home/ivan/appTest/room/", name)
      if(filename != ""){
        Room.save(room.copy(name=name, roomType=roomType, picture=filename))
      }else{
        Room.save(room.copy(name=name, roomType=roomType))
      }
      changeView(views.html.profile.dean.rooms.list())
  }

  def removeRoom(id: String) = withUser { user => implicit request =>
      Room.delete(id)
      Redirect(routes.RoomsController.roomsList())
  }
}
