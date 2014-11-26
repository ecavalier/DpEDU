package controllers

import play.api.mvc._
import model.users.User
import play.api.Logger
import org.joda.time.DateTime


trait Secured {

  val role : Array[String]

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index())

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
    writeToLog(request, username)
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
    writeToLog(request, username)
    User.findByEmail(username).map { user =>
      val x = currentRole.map(_.getName)
      if(x contains (user.getClass.getName+"$")){
        f(user)(request)
      }else{
        onUnauthorized(request)
      }
    }.getOrElse(onUnauthorized(request))
  }

  def writeToLog(request: Request[AnyContent], username: String) =
    Logger.info("\nGetting request with uri:" + request.uri + "\nfrom user " + username + "\nip:" +  request
      .remoteAddress + "\n" + DateTime.now())
}
