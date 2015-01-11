package models

import scala.slick.driver.MySQLDriver
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyQuery

case class Addon (
  id: Option[Int] = None,
  name: String,
  price: Int,
  contentUrl: String
)

class Addons(tag: Tag) extends Table[Addon](tag, "Addons") {
  def name = column[String]("TITLE")
  def price = column[Int]("PRICE")
  def contentUrl = column[String]("CONTENTURL")
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (id.?, name, price, contentUrl) <> (Addon.tupled, Addon.unapply)
 //Foreign Key to Booking
}

object Addons extends DAO {

  def insert(addon: Addon)(implicit s: Session) {
    Addons.insert(addon)
  }

  def list(implicit s: Session): List[Addon] = Addons.list

  def getById(id: Int)(implicit s:Session): Addon = {
    Addons.filter(_.id === id).first
  }

}