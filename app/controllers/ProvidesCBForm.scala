package controllers

import play.api.data.Form
import models.{SubscribeForm, Info, CallBack}
import play.api.data.Forms._
import play.api.mvc._
import play.api._

/**
 * Created by d1sp on 09.02.14.
 */
trait ProvidesCBForm {
  /* Mapping Form to CallBack model */
  implicit val callbackForm : Form[CallBack] = Form(
    mapping(
      "username" -> nonEmptyText,
      "tel" -> nonEmptyText,
      "url" -> text
    )(CallBack.apply)(CallBack.unapply))

  implicit val subscribeForm : Form[SubscribeForm] = Form(
    mapping(
      "email" -> nonEmptyText,
      "url" -> nonEmptyText
    )(SubscribeForm.apply)(SubscribeForm.unapply))

  implicit val info: Info = Info("+7 499 647 50 81", "+371 28 28 28 57", "+371 66 16 35 40", "Luxury-отель в Риге. Роскошь в деталях", "http://tour.riga-royal.ru/pano/viesnica/_flash/")

}
