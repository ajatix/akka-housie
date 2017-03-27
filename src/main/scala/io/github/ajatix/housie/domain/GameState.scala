package io.github.ajatix.housie.domain

import akka.persistence.fsm.PersistentFSM.FSMState

/**
  * Created by ajay on 24/03/17.
  */
sealed trait GameState extends FSMState

case object Registering extends GameState {
  override def identifier = "registering"
}

case object Readying extends GameState {
  override def identifier = "readying"
}

case object Running extends GameState {
  override def identifier = "running"
}

case object Reviewing extends GameState {
  override def identifier = "reviewing"
}