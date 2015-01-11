package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.Play.current
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models._
import com.typesafe.plugin._

object Contacts extends Controller with ProvidesCBForm {

  implicit val contactsForm : Form[ContactsForm] = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "question" -> nonEmptyText
    )(ContactsForm.apply)(ContactsForm.unapply))

  def index = DBAction { implicit request =>
    Ok(views.html.contacts.contactsmain("Контакты гостиница Риги Royal Casino SPA and Hotel Resort"))
  }

  def send = Action { implicit request =>
    contactsForm.bindFromRequest.fold(
      errors => {
        BadRequest
      },
      sendBookData => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Вопрос из контактов Riga-Royal.ru")
        mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com", "karina@royalhotel.lv", "reception@royalhotel.lv")
        mail.setFrom("Royal Casino SPA&Hotel Resort <julia@riga-royal.ru>")
        mail.send( ( "Имя: " + sendBookData.name + "\n" +
          "Email: " + sendBookData.email + "\n" +
          "Вопрос: " + sendBookData.question + "\n"
          ) )
        Ok
      }
    )
  }

}
