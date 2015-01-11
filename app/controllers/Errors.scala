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

object Errors extends Controller with ProvidesCBForm {

  def error404 = Action { implicit request =>
    Ok(views.html.errors.notfound("404 ошибка - Riga-Royal.ru"))
  }

}
