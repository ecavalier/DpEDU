package model

import javax.persistence.Entity
import play.db.ebean.Model

/**
 * Created by Ivan Yatcuba on 9/6/14.
 */
case class UserData(email: String, password: String)
@Entity
class User( var email: String,
            var password: String,
            var fullName: String
            ) extends Model {

  var isAdmin: Boolean = false

  def this() = this(null, null, null)
}
