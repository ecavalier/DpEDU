package controllers

import play.api.mvc._
import play.api.libs.Files
import se.radley.plugin.salat
import play.api.Play.current

object Application extends Controller {

  def index = Action {
    Ok(views.html.index.index())
  }

  def pictureUpload(req: Request[MultipartFormData[Files.TemporaryFile]], strMap: String, dirPath: String,
                    newFileName: String): String  = {
    var filename = ""
    req.body.file(strMap).map { picture =>
      filename = newFileName
      val gridFs = salat.gridFS("photos")
      gridFs.remove(filename)
      val uploadedFile = gridFs.createFile(picture.ref.file)
      uploadedFile.filename = filename
      uploadedFile.contentType = picture.contentType.orNull
      uploadedFile.save()
    }
    filename
  }

}
