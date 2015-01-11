package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.typesafe.plugin._
import scala.util.matching.Regex
import play.api.mvc.Results._

/**
 * Created by d1sp on 29.01.14.
 */
object RoomController extends Controller with ProvidesCBForm{

  /*Pages*/
  def rooms = DBAction { implicit request =>
    Ok(views.html.rooms.roomsmain("Роскошные апартаменты в Риге: комфортные условия и доступные цены", RoomListings.listAll))
  }

  def roomPage(name: String) = DBAction { implicit request =>

    val currentSession = request.session

    val dateIn = currentSession.get("dateIn")
    val dateOut = currentSession.get("dateOut")
    val people = currentSession.get("people")

    val pattern = "(?i)".r
    val Pattern = "(?i)".r

    name match {
      case "paris" => Ok(views.html.rooms.paris.parismain("Номер Париж в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Paris")), dateIn, dateOut, people))
      case "dubai" => Ok(views.html.rooms.dubai.dubaimain("Номер Дубай в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Dubai")), dateIn, dateOut, people))
      case "venice" => Ok(views.html.rooms.venice.venicemain("Номер Венеция в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Venice")), dateIn, dateOut, people))
      case "vienna" => Ok(views.html.rooms.vienna.viennamain("Номер Вена в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Vienna")), dateIn, dateOut, people))
      case "moscow" => Ok(views.html.rooms.moscow.moscowmain("Номер Москва в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Moscow")), dateIn, dateOut, people))
      case "newyork" => Ok(views.html.rooms.newyork.newyorkmain("Номер Нью-Йорк в отеле Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("New York")), dateIn, dateOut, people))
      case "versace" => Ok(views.html.rooms.versace.versacemain("Номер Версаче в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Versace")), dateIn, dateOut, people))
      case "barcelona" => Ok(views.html.rooms.barcelona.barcelonamain("Номер Барселона в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Barcelona")), dateIn, dateOut, people))
      case "london" => Ok(views.html.rooms.london.londonmain("Номер Лондон в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("London")), dateIn, dateOut, people))
      case "lasvegas" => Ok(views.html.rooms.lasvegas.lasvegasmain("Номер Лас Вегас в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Las Vegas")), dateIn, dateOut, people))
      case "montecarlo" => Ok(views.html.rooms.montecarlo.montecarlomain("Номер Монте Карло в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Monte Carlo")), dateIn, dateOut, people))
      case "amsterdam" => Ok(views.html.rooms.amsterdam.amsterdammain("Номер Амстердам в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Amsterdam")), dateIn, dateOut, people))
      case "roulette" => Ok(views.html.rooms.roulette.roulettemain("Номер Рулетка в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Roulette")), dateIn, dateOut, people))
      case "poker" => Ok(views.html.rooms.poker.pokermain("Номер Покер в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Poker")), dateIn, dateOut, people))
      case "blackjack" => Ok(views.html.rooms.blackjack.blackjackmain("Номер Блэк Джек в отеле и гостинице Royal Casino Рига", Rooms.updatePrice(Rooms.getRoomDetails("Black Jack")), dateIn, dateOut, people))
      case _ => NotFound(views.html.errors.notfound("404 ошибка - Riga-Royal.ru"))
    }
  }

  // Readers for Ajax

  implicit val congratsReader = (
    (__ \ 'email).read[String] and
    (__ \ 'prize).readNullable[String]
    tupled
    )

  implicit val emailReader = (
    (__ \ 'email).read[String]
    )

  def wheelCongrats = Action(parse.json) { request =>
    val json = request.body

    json.validate[(String, Option[String])].fold (
      valid =  res => {
        // Email to us
        val mail = use[MailerPlugin].email
        mail.setSubject("Выигрыш в рулетку Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com")
        mail.setFrom("Riga-Royal Administration <rigaroyal@rigaroyal.com>")
        mail.send( "Email: " + res._1 + "\n" + "Prize: " + res._2.getOrElse("No prize"))

        // Email to client
        Ok
      },
      invalid =  e => BadRequest(e.toString)
    )
  }

  def wheelTry = Action(parse.json) { request =>
    val json = request.body

    json.validate[(String)].fold (
      valid =  res => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Повторная попытка в рулетку Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com", "karina@royalhotel.lv", "reception@royalhotel.lv")
        mail.setFrom("Riga-Royal Administration <rigaroyal@rigaroyal.com>")
        mail.send( "Email: " + res)
        Ok
      },
      invalid =  e => BadRequest(e.toString)
    )
  }

}
