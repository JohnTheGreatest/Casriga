import controllers.ProvidesCBForm
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

/**
 * Mix this trait in your GlobalSettings object to generate a 401 Moved Permanently
 * from any /url/ to /url.
 */
trait TrailingSlash extends GlobalSettings with ProvidesCBForm {
  override def onHandlerNotFound(request: RequestHeader): Future[SimpleResult] =
    if (request.path.endsWith("/"))
      Future.successful(MovedPermanently(request.path.dropRight(1)))
    else {
      Future.successful(NotFound(views.html.errors.notfound("404 ошибка - Riga-Royal.ru")))
      /* super.onHandlerNotFound(request)*/
    }

}