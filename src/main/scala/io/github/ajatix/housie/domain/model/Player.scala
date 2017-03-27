package io.github.ajatix.housie.domain.model

import akka.actor.ActorRef

/**
  * Created by ajay on 25/03/17.
  */
case class Player(name: String) {
  def attachActor(actor: ActorRef): PlayerWithActor = PlayerWithActor(this, actor)
}

case class PlayerWithActor(player: Player, actor: ActorRef) {
  def name = player.name
}