package io.github.ajatix.housie.common

import java.util.UUID

/**
  * Created by ajay on 25/03/17.
  */
object Helpers {

  def now: Long = System.currentTimeMillis()
  def id: String = UUID.randomUUID().toString
  def randomize(prefix: String) = s"$prefix-$id"

  val readyMessage = "ready".r
  val startMessage = "start".r
  val stopMessage = "stop".r
  val quitMessage = "quit".r
  val claimMessage = "claim (.+)".r
  val restartMessage = "restart".r

}
