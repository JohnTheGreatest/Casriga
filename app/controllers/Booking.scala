package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.github.nscala_time.time.Imports._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import java.util.Locale
import models._
import play.api.data.Form
import play.api.data.Forms._
import com.typesafe.plugin._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import play.filters.csrf.Global

/**
 * Created by d1sp on 14.02.14.
 */
object Booking extends Controller with ProvidesCBForm {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def step1 = Action { implicit request =>
    val currentSession = request.session

    val dateIn = currentSession.get("dateIn")
    val dateOut = currentSession.get("dateOut")
    val people = currentSession.get("people")

    Ok(views.html.booking.step1.step1main("Забронировать отель в Риге онлайн: лучшие номера для Вашего удобства", dateIn, dateOut, people))
  }

  // JSON Readers

  implicit val bookingReadOne = (
    (__ \ "dateIn").read[String] and
    (__ \ "dateOut").read[String] and
    (__ \ "people").read[String]
    tupled
  )

  implicit val bookingReadTwo = (
    (__ \ 'rooms).read[String] and
    (__ \ 'price).read[String]
    tupled
  )

  implicit val bookingReadThree = (
    (__ \ 'addons).read[String]
  )

  // ################################

  def step1save = Action(parse.json) { implicit request =>
    val json = request.body

    json.validate[(String, String, String)].fold (
      valid =  res => {
          Ok
            .withSession(request.session +("dateIn" -> res._1) + ("dateOut" -> res._2) + ("people" -> res._3))
      },
      invalid =  e => BadRequest(e.toString)
    )
  }

  def step2 = Action { implicit request =>
    play.api.db.slick.DB.withSession { implicit session:play.api.db.slick.Config.driver.simple.Session =>

    val currentSessionDateIn = request.session.get("dateIn").getOrElse("None")
    val currentSessionTs = request.session.get("ts")

    currentSessionDateIn match {
      case "None" => Redirect(routes.Booking.step1) // There is no data in Cookies, redirect to step1
      case _ =>  {

        val dtfEn = DateTimeFormat.forPattern("E MMM d y").withLocale(Locale.ENGLISH) // Joda Date Format En
        val dateIn = dtfEn.parseDateTime(request.session.get("dateIn").getOrElse("None"))
        val dateOut = dtfEn.parseDateTime(request.session.get("dateOut").getOrElse("None"))
        val delta = new Duration(dateIn, dateOut).getStandardDays.toInt // Duration of periods

        // Translate to RUS DateTime
        val dateInRus = dateIn.toString("E MMM d y", new Locale("ru", "RU"))
        val dateOutRus = dateOut.toString("E MMM d y", new Locale("ru", "RU"))

        val changedList:List[(Room, List[Feature])] = for {r <- Rooms.list.sortBy(_.price)} yield (r.copy(price = (r.price*Currencys.first.value).toInt), models.Features.getFeatures(r))
        Ok(views.html.booking.step2.step2main("Booking | Step2", delta, dateInRus, dateOutRus, changedList))
            .withSession(request.session + ("dateInRus" -> dateInRus) + ("dateOutRus" -> dateOutRus) + ("ts" -> "yes"))


        // Convert EUR to RUB
        // Access API
       /*WS.url("http://rate-exchange.appspot.com/currency?from=EUR&to=RUB")
       .get
       .map { r =>
          val currency = Json.parse(r.body).\("rate").as[Double]
          val changedList:List[(Room, List[Feature])] = for {r <- Rooms.list.sortBy(_.price)} yield (r.copy(price = (r.price*currency).toInt), models.Features.getFeatures(r))
          Ok(views.html.booking.step2.step2main("Booking | Step2", delta, dateInRus, dateOutRus, changedList))
            .withSession(request.session + ("dateInRus" -> dateInRus) + ("dateOutRus" -> dateOutRus) + ("ts" -> "yes"))
        }.recoverWith { case _ =>
         val changedList:List[(Room, List[Feature])] = for {r <- Rooms.list.sortBy(_.price)} yield (r.copy(price = r.price*50), models.Features.getFeatures(r))
         Future{Ok(views.html.booking.step2.step2main("Booking | Step2", delta, dateInRus, dateOutRus, changedList))
           .withSession(request.session + ("dateInRus" -> dateInRus) + ("dateOutRus" -> dateOutRus) + ("ts" -> "yes"))}
       }*/

      }
    }
    }
  }

  def step2save = Action(parse.json) { implicit request =>
    val json = request.body
    // Last session plus current
    json.validate[(String, String)].fold (
      valid =  res => {
        Ok
          .withSession(request.session + ("room" -> res._1) + ("price" -> res._2.replace("&nbsp;","")))
      },
      invalid =  e => BadRequest(e.toString)
    )
  }

  def step3 = DBAction { implicit request =>

    val currentSessionDateIn = request.session.get("room").getOrElse("None")

    currentSessionDateIn match {
      case "None" => Redirect(routes.Booking.step1) // There is no data in Cookies, redirect to step1
      case _ => {
        Ok(views.html.booking.step3.step3main("Booking | Step3", request.session.get("dateInRus").getOrElse("None"), request.session.get("dateOutRus").getOrElse("None"), Addons.list))
      }
    }
  }

  def step3save = DBAction(parse.json) { implicit request =>
    val json = request.body
    // Last session plus current
    json.validate[(String)].fold (
      valid =  res => {
        Ok
          .withSession(request.session + ("addons" -> res))
      },
      invalid =  e => BadRequest(e.toString)
    )
  }

  // Provide BookingForm on step4
  implicit val bookingForm : Form[BookingForm] = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "number" -> nonEmptyText
    )(BookingForm.apply)(BookingForm.unapply))

  def step4 = DBAction { implicit request =>

    val currentSessionAddons = request.session.get("addons").getOrElse("None")

    currentSessionAddons match {
      case "None" => Redirect(routes.Booking.step2) // There is no data in Cookies, redirect to step1
      case _ => {
        Ok(views.html.booking.step4.step4main("Booking | Step4"))
      }
    }
  }

  def send = DBAction { implicit request =>

    val currentSessionAddons = request.session.get("addons").getOrElse("None")

    currentSessionAddons match {
      case "None" => Redirect(routes.Booking.step1)
      case _ => {
        //Take Addons
        val mapped: Array[Int] = request.session.get("addons").getOrElse("0").split(",").map(_.toInt).distinct
        val addonList: List[Addon] = for {m <- mapped.toList} yield (Addons.getById(m))

        // Prepare all data to send
        val dateIn = request.session.get("dateInRus").getOrElse("None")
        val dateOut = request.session.get("dateOutRus").getOrElse("None")
        val ppl = request.session.get("people").getOrElse("None")
        val room = request.session.get("room").getOrElse("None")
        val price = request.session.get("price").getOrElse("None")

        bookingForm.bindFromRequest.fold(
          errors => {
            BadRequest(views.html.booking.step4.step4main("Booking | Step4 - Some Errors"))
          },
          sendBookData => {
            val mail = use[MailerPlugin].email
            mail.setSubject("Заказ Riga-Royal.ru")
            mail.setRecipient("Wow <van4oys@gmail.com>", "van4oys@gmail.com", "karina@royalhotel.lv", "reception@royalhotel.lv")
            mail.setFrom("Royal Casino SPA&Hotel Resort <julia@riga-royal.ru>")
            mail.send( ("Дата заезда: " + dateIn + "\n" +
              "Дата выезда: " + dateOut + "\n" +
              "Количество человек: " + ppl + "\n" +
              "Номер: " + room + "\n" +
              "Стоимость: " + price + "\n" +
              "Дополнения: " + addonList.map {
              a =>
                a.name + " " +
                  a.price
            } + "\n" +
              "Name: " + sendBookData.name + "\n" +
              "Email: " + sendBookData.email + "\n" +
              "Tel: " + sendBookData.number + "\n"
              ) )
            Ok
          }
        )
      }
    }

  }



}
