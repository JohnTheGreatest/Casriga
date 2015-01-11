package models

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyQuery

case class FeatureHub (
                  id: Option[Int] = None,
                  roomsId: Option[Int] = None,
                  featureId: Option[Int] = None
                  )

/* Schema for FeaturesHubTable */
class FeaturesHub(tag: Tag) extends Table[FeatureHub](tag, "FeaturesHub") {

  def roomId = column[Int]("ROOM_ID", O.Nullable)
  def featureId = column[Int]("FEATURE_ID", O.Nullable)
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (id.?, roomId.?, featureId.?) <> (FeatureHub.tupled, FeatureHub.unapply)

  //Foreign key to Something
  def rooms: ForeignKeyQuery[Rooms, Room] = foreignKey("ROOM_FK", roomId, TableQuery[Rooms])(_.id)
  def features: ForeignKeyQuery[Features, Feature] = foreignKey("FEATURE_FK", featureId, TableQuery[Features])(_.id)

}

object FeaturesHub extends DAO {

  def insert(fhub: FeatureHub)(implicit s:Session) {
    FeaturesHub.insert(fhub)
  }

}