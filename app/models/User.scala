/*
package models

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

case class User (
  fio: String,
  email: String,
  tel: String,
  id: Int
)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def fio = column[String]("FIO")
  def email = column[String]("EMAIL")
  def tel = column[String]("TEL")
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def * = (fio, email, tel, id) <> (User.tupled, User.unapply)
}
*/
