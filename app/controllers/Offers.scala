package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import com.typesafe.plugin._
import views.html
import play.api.data.Form
import play.api.data.Forms._
import models.{BookingForm, CallBack, OfferForm}
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
/**
 * Created by d1sp on 01.04.14.
 */
object Offers extends Controller with ProvidesCBForm {

  implicit val offerForm : Form[OfferForm] = Form(
    mapping(
      "dateIn" -> nonEmptyText,
      "dateOut" -> nonEmptyText,
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "number" -> nonEmptyText,
      "url" -> nonEmptyText
    )(OfferForm.apply)(OfferForm.unapply))


  def offer(name: String) = DBAction { implicit request =>

    name match {
      case "wedding" => Ok(views.html.offers.wedding.weddingmain("Акция свадебная ночь в отеле Риги Royal Casino SPA and Hotel Resort"))
      case "golf" => Ok(views.html.offers.golf.golfmain("Акция гольф и теннис вместе с отелем Риги Royal Casino SPA and Hotel Resort"))
      case "spa" => Ok(views.html.offers.spa.spamain("Акция спа-тур вместе с отелем Риги Royal Casino SPA and Hotel Resort"))
      case "excursion" => Ok(views.html.offers.excursion.excursionmain("Экскурсия по старой Риге вместе с отелем Royal Casino"))
      case "party" => Ok(views.html.offers.party.partymain("Вечеринка в шикарном номере вместе с гостиницей Royal Casino"))
      case "birthday" => Ok(views.html.offers.birthday.birthdaymain("Предлагаем отметить День рождения в Риге - столице Латвии"))
    }

  }

  def send = Action { implicit request =>
    offerForm.bindFromRequest.fold(
      errors => {
        BadRequest
      },
      sendBookData => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Заказ по акции Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com", "karina@royalhotel.lv", "reception@royalhotel.lv")
        mail.setFrom("Royal Casino SPA&Hotel Resort <julia@riga-royal.ru>")
        mail.send( ("Дата заезда: " + sendBookData.dateIn + "\n" +
          "Дата выезда: " + sendBookData.dateOut + "\n" +
          "Имя: " + sendBookData.name + "\n" +
          "Email: " + sendBookData.email + "\n" +
          "Телефон: " + sendBookData.number + "\n" +
          "Акция: " + sendBookData.url + "\n"
          ) )
        Ok
      }
    )
  }
}
