import com.mongodb.casbah.Imports._
import model.users.{DeanManager, User}
import play.api._


object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if(User.count(DBObject(), Nil, Nil) == 0){
      User.insert(new DeanManager(email="admin", password="1"))
    }
  }

}
