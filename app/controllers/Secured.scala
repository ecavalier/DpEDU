package controllers

import play.api.mvc._
import model.users.User


trait Secured {

  val role : Array[String]

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index())

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    User.findByEmail(username).map { user =>
      if(role contains (user.getClass.getName+"$")){
        f(user)(request)
      }else{
        onUnauthorized(request)
      }
    }.getOrElse(onUnauthorized(request))
  }

  def withUser(currentRole: Array[Class[_]])(f: User => Request[AnyContent] => Result) = withAuth { username =>
    implicit request =>
    User.findByEmail(username).map { user =>
      val x = currentRole.map(_.getName)
      if(x contains (user.getClass.getName+"$")){
        f(user)(request)
      }else{
        onUnauthorized(request)
      }
    }.getOrElse(onUnauthorized(request))
  }
}
