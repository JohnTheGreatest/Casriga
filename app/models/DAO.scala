package models

import play.api.db.slick.Config.driver.simple._


// * Created by d1sp on 21.02.14.

trait DAO {
  lazy val Rooms = TableQuery[Rooms]
  lazy val FeaturesHub = TableQuery[FeaturesHub]
  lazy val Features = TableQuery[Features]
  lazy val RoomListings = TableQuery[RoomListings]
  lazy val Addons = TableQuery[Addons]
  lazy val Currencys = TableQuery[Currencys]
}
