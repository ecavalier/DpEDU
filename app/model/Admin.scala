package model

import com.novus.salat.annotations.raw.Key
import org.bson.types.ObjectId

/**
 * Created by Ivan Yatcuba on 9/8/14.
 */
case class Admin(@Key("_id") id: ObjectId = new ObjectId, email: String, password: String, fullName: String) extends
                                             User(id, email, password, fullName){
  override val isAdmin: Boolean = true
}
