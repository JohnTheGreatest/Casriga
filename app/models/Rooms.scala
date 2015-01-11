package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyQuery
import models._
import models.Feature
import models.RoomListing
import models.Room

case class Room (
                     id: Option[Int] = None,
                     name: String,
                     photoUrl: String,
                     photoAlt: String,
                     text: String,
                     price: Int,
                     discount: Option[Int] = None,
                     squaremeters: Int,
                     listingId: Option[Int] = None
                     )

/* Schema for Rooms */
class Rooms(tag: Tag) extends Table[Room](tag, "Rooms") {

  def name = column[String]("NAME")
  def photoUrl = column[String]("PHOTOURL")
  def photoAlt = column[String]("PHOTOALT")
  def text = column[String]("TEXT", O.DBType("TEXT"))
  def price = column[Int]("PRICE")
  def squaremeters = column[Int]("SQUARE")
  def discount = column[Int]("DISCOUNT", O.Nullable)
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def listingId = column[Int]("LISTING_ID", O.Nullable)

  def * = (id.?, name, photoUrl, photoAlt, text, price, discount.?, squaremeters, listingId.?) <> (Room.tupled, Room.unapply)

  def listings: ForeignKeyQuery[RoomListings, RoomListing] = foreignKey("listing_fk", listingId, TableQuery[RoomListings])(_.id)

}

object Rooms extends DAO {

  def insert(room: Room)(implicit s:Session) {
    Rooms.insert(room)
  }

  def count(implicit s:Session): Int = Query(Rooms.length).first

  def list(implicit s:Session): List[Room] = Rooms.list

  def listAllStatement(implicit s:Session) =
    (for {
      r <- Rooms
      fh <- FeaturesHub if (fh.roomId === r.id)
      f <- Features if (fh.featureId === f.id)
    } yield (r, f)).selectStatement

  // Take all Rooms and associated Features
  def listAll(implicit s:Session): List[(Room, List[Feature])] = for{r <- list.sortBy(_.price)} yield (r, models.Features.getFeatures(r))

  def getRoomDetails(name: String)(implicit s:Session): Room = Rooms.filter(_.name === name).first

  def updatePrice(r: Room)(implicit s:Session): Room = r.copy(price = (r.price*Currencys.first.value).toInt)

}

