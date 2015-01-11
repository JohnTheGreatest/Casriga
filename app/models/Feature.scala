package models

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyQuery
import models.{Features, Rooms}

/**
 * Created by d1sp on 03.03.14.
 */
case class Feature (
  id: Option[Int] = None,
  iconUrl: String,
  text: String
                     )

class Features(tag: Tag) extends Table[Feature](tag, "Feature") {

  def iconUrl = column[String]("ICONURL")
  def text = column[String]("TEXT")
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (id.?, iconUrl, text) <> (Feature.tupled, Feature.unapply)

}

object Features extends DAO {

  def insert(feature: Feature)(implicit s: Session) {
    Features.insert(feature)
  }

  def getFeatures(room: Room)(implicit s:Session): List[Feature] =
    (for {
      fh <- FeaturesHub if (fh.roomId === room.id)
      f <- Features if (fh.featureId === f.id)
    } yield (f)).list


  /* def getFeatures(room: RoomListing)(implicit s:Session): List[Feature] =
      (for {
        r <- Rooms if (r.listingId === room.id)
        fh <- FeaturesHub if (fh.roomId === r.id)
        f <- Features if (fh.featureId === f.id)
      } yield (f)).list*/
}