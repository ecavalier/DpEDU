package controllers

import play.api.mvc._
import play.api.libs.Files
import se.radley.plugin.salat
import play.api.Play.current
import java.text.SimpleDateFormat
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index.index())
  }

  def pictureUpload(req: Request[AnyContent], strMap: String, dirPath: String,
                    newFileName: String): String  = {
    var filename = ""
    req.body.asMultipartFormData.get.file(strMap).map { picture =>
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


  def picture(name: String) = Action {

    import com.mongodb.casbah.Implicits._

    val gridFs = salat.gridFS("photos")

    gridFs.findOne(Map("filename" -> name)) match {
      case Some(f) => SimpleResult(
        ResponseHeader(OK, Map(
          CONTENT_LENGTH -> f.length.toString,
          CONTENT_TYPE -> f.contentType.getOrElse(BINARY),
          DATE -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.US).format(f.uploadDate)
        )),
        Enumerator.fromStream(f.inputStream)
      )

      case None => NotFound
    }
  }
}
