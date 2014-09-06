package controllers

import play.api.mvc._


/**
 * Created by Ivan Yatcuba on 9/6/14.
 */
object Login extends Controller {



  def login = Action {
    Ok(views.html.profile.admin.adminLayout())
  }

}
