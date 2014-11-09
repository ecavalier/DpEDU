package controllers

import play.api.mvc._
import play.api.libs.Files

object Application extends Controller {

  def index = Action {
    Ok(views.html.index.index())
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
