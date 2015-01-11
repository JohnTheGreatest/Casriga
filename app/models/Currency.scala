package models

import play.api.libs.ws.WS
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.db.slick.Config.driver.simple._
import play.api.Logger
import scala.slick.lifted.ForeignKeyQuery

/**
 * Created by d1sp on 07.04.14.
 */
case class Currency (
  id: Option[Int] = None,
  from: String,
  to: String,
  value: Double
)

class Currencys(tag: Tag) extends Table[Currency](tag, "Currencys") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def from = column[String]("FROM")
  def to = column[String]("TO")
  def value = column[Double]("VALUE")

  def * = (id.?, from, to, value) <> (Currency.tupled, Currency.unapply)
}

object Currencys extends DAO {

  def insert(cur: Currency)(implicit s:Session) {
    Currencys.insert(cur)
  }

  def update(cur: Currency)(implicit s:Session) {
    Currencys.where(_.id === cur.id).update(cur)
  }

  def count(implicit s:Session): Int = Query(Currencys.length).first

  def list(implicit s:Session): List[Currency] = Currencys.list

  def first(implicit s:Session): Currency = Currencys.first()

  def getCurrencyFromServer(implicit s:Session) = {
    implicit val context = scala.concurrent.ExecutionContext.Implicits.global
    WS.url("http://rate-exchange.appspot.com/currency?from=EUR&to=RUB")
      .get
      .map { r =>
      val currency = Json.parse(r.body).\("rate").as[Double]

      if (this.count == 0) {
        this.insert(Currency(Option(1), "EUR", "RUB", currency))
      } else {
        this.update(Currency(Option(1), "EUR", "RUB", currency))
      }

    }.recoverWith { case _ =>
      Logger.info("Something went wrong with WS")
      Future{
        if (this.count == 0) {
          this.insert(Currency(Option(1), "EUR", "RUB", 49))
        } else {
          this.update(Currency(Option(1), "EUR", "RUB", 49))
        }
      }
    }
  }

}

