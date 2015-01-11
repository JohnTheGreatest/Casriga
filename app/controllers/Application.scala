package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.typesafe.plugin._
import views.html
import play.api.data.Form
import play.api.data.Forms._
import models.{Currencys, Room, Rooms, CallBack}
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import org.h2.util.MathUtils

object Application extends Controller with ProvidesCBForm {

  def index = DBAction { implicit request =>

    val currentSession = request.session

    val dateIn = currentSession.get("dateIn")
    val dateOut = currentSession.get("dateOut")
    val people = currentSession.get("people")

    val rooms:List[Room] = for {r <- Rooms.list if(r.name == "New York" || r.name == "Versace")} yield (r)

    Ok(views.html.index.main("Лучшая гостиница Риги: комфортные номера и гибкие цены", dateIn, dateOut, people, rooms.map{r => r.copy(price = (r.price*Currencys.first.value).toInt)}))
  }

  def send = Action { implicit request =>
    callbackForm.bindFromRequest.fold(
      errors => {
        if(!errors.hasErrors) {
          Redirect(routes.Application.index())
        }
        BadRequest(views.html.elements.callbackform(errors))
      },
      callBack => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Обратный звонок Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com", "karina@royalhotel.lv", "reception@royalhotel.lv")
        mail.setFrom("Royal Casino SPA&Hotel Resort <julia@riga-royal.ru>")
        mail.send( "Name: " + callBack.username + "\n" + "Tel: " + callBack.tel + "\n" + "From page: " + callBack.url)
        Ok
      }
    )
  }

  def sendSubscribe = Action { implicit request =>
    subscribeForm.bindFromRequest.fold(
      errors => {
        if(!errors.hasErrors) {
          Redirect(routes.Application.index())
        }
        BadRequest
      },
      subscribeForm => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Подписка на новости Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com")
        mail.setFrom("Royal Casino SPA&Hotel Resort <julia@riga-royal.ru>")
        mail.send( "Email: " + subscribeForm.email + "\n" + "From page: " + subscribeForm.url)
        Ok
      }
    )
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRouter")(
        routes.javascript.Application.send,
        routes.javascript.Application.sendSubscribe,
        routes.javascript.Booking.send,
        routes.javascript.RoomController.wheelCongrats,
        routes.javascript.RoomController.wheelTry,
        routes.javascript.Offers.send,
        routes.javascript.Contacts.send
      )
    ).as("text/javascript")
  }

}