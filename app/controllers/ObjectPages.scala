package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models._
import play.api.mvc.Results._

object ObjectPages extends Controller with ProvidesCBForm{

  /* Casino */

  def casinoMain = DBAction { implicit request =>
    Ok(views.html.objects.casino.casinomain("Казино тур - лучший отпуск для любителей острых ощущений"))
  }

  def casinoArticlesMain = DBAction { implicit request =>
    Ok(views.html.objects.casino.blog.blogmain("Лучшие статьи и заметки Рунета о казино в Риге"))
  }

  def casinoArticles(name: String) = DBAction { implicit request =>
    name match {
      case "samoe-dorogoe-kazino" =>  Ok(views.html.objects.casino.blog.article1.article1main("Топ пять самых дорогих казино мира: богатые и знаменитые заведения"))
      case "vigrish-v-kazino" =>  Ok(views.html.objects.casino.blog.article2.article2main("Самый крупный выигрыш в казино: как разбогатеть за один вечер"))
      case "samoe-izvestnoe-kazino" =>  Ok(views.html.objects.casino.blog.article3.article3main("Самые известные казино мира: топ-4 лучших игорных заведений"))
      case _ => NotFound(views.html.errors.notfound("404 ошибка - Riga-Royal.ru"))
    }

  }

  /* SPA */

  def spaMain = Action { implicit request =>
    Ok(views.html.objects.casino.casinomain("Royal Casino SPA: лучшие цены на СПА-процедуры в Риге"))
  }

}
