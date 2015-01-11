package models

/**
* Created by d1sp on 29.01.14.
*/
case class CallBack  (
  username: String,
  tel: String,
  url: String
)

object CallBack {

  def send(callback: CallBack) {
    println(s"Name to send mail: $callback.username Telephone: $callback.tel")
  }
}


