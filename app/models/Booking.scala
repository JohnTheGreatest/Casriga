/*
package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.ForeignKeyQuery

case class Booking (
  dateIn: String,
  dateOut: String,
  people: Int,
  room: String,
  addonId: Int,
  userId: Int,
  id: Int
)

/* Schema for Booking Class */
class Bookings(tag: Tag) extends Table[Booking](tag, "BOOKINGS") {

  def dateIn = column[String]("DATEIN")
  def dateOut = column[String]("DATEOUT")
  def people = column[Int]("PEOPLE")
  def room = column[String]("ROOM")
  def addonId = column[Int]("ADDON_ID")
  def userId = column[Int]("USER_ID")
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (dateIn, dateOut, people, room, addonId, userId, id) <> (Booking.tupled, Booking.unapply)

  //Foreign key to Addon
  def addons: ForeignKeyQuery[Addons, Addon] = foreignKey("ADD_FK", addonId, TableQuery[Addons])(_.id)

  def users: ForeignKeyQuery[Users, User] = foreignKey("USR_FK", userId, TableQuery[Users])(_.id)

}

object Bookings extends DAO {

  def insert(booking: Booking)(implicit s:Session) {
    Bookings.insert(booking)
  }
}

*/
