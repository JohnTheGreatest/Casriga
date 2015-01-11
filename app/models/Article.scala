package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyQuery
import models._

case class Article (
                         id: Option[Int] = None,
                         url: String,
                         title: String,
                         description: String,
                         imageUrl: String,
                         imageAlt: String
                         )

/* Schema for Rooms */
/*
class RoomListings(tag: Tag) extends Table[RoomListing](tag, "RoomListings") {

  def name = column[String]("NAME")
  def photoUrl = column[String]("PHOTOURL")
  def photoAlt = column[String]("PHOTOALT")
  def iconUrl = column[String]("ICONURL")
  def ctatext = column[String]("CTATEXT")
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (id.?, name, photoUrl, photoAlt, iconUrl, ctatext) <> (RoomListing.tupled, RoomListing.unapply)

}

object RoomListings extends DAO {

  def insert(r: RoomListing)(implicit s:Session) {
    RoomListings.insert(r)
  }

  def list(room: Room)(implicit s:Session): RoomListing = RoomListings.filter(_.id === room.listingId).first

  def list (implicit s:Session): List[(Room, RoomListing)] =
    (for {
      r <- Rooms
      rl <- RoomListings if(rl.id === r.listingId)
    } yield (r, rl)).list

  // Take all RoomListings and associated Features
  def listAll(implicit s:Session): List[(Room, RoomListing, List[Feature])] =
    for {
      a <- list
    } yield (a._1, a._2, models.Features.getFeatures(a._1))

}*/
