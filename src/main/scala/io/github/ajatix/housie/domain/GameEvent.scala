package io.github.ajatix.housie.domain

import akka.actor.ActorRef
import io.github.ajatix.housie.common.Helpers
import io.github.ajatix.housie.domain.model._

/**
  * Created by ajay on 24/03/17.
  */
trait GameEvent

case class PlayerJoining(player: Player, actor: ActorRef) extends GameEvent
case class PlayerReady(playerName: String) extends GameEvent
case class PlayerQuitting(playerName: String) extends GameEvent

case object PlayersReady extends GameEvent
case object StartGame extends GameEvent
case object StopGame extends GameEvent
case object RestartGame extends GameEvent

case class PlayerClaimRequest(id: String, playerName: String, prizeName: String) extends GameEvent
case class PlayersChanged(players: Iterable[Player]) extends GameEvent
case class PlayerInformation(player: Player) extends GameEvent
case object RegistrationClosed extends GameEvent
case object GameStarting extends GameEvent
case object TilesOver extends GameEvent
case object PrizeAlreadyClaimed extends GameEvent
case object PrizeClaimInvalid extends GameEvent
case class PrizeClaimed(prize: String, playerName: String) extends GameEvent
case object EndGame extends GameEvent
case class TileGenerated(tile: Tile) extends GameEvent

case class PlayerAdded(player: PlayerWithActor) extends GameEvent
case class PlayerRemoved(playerName: String) extends GameEvent
case class PlayerJoined(playerName: String) extends GameEvent
case class PlayerQuit(playerName: String) extends GameEvent
case class PlayersInRoom(players: Iterable[String]) extends GameEvent

case object GenerateCards extends GameEvent
case class PlayerCard(card: Card) extends GameEvent

case object GetNextTile extends GameEvent
case class AddTile(tile: Tile) extends GameEvent
case class ProcessClaim(claim: Claim) extends GameEvent

case class DisplayWinners(claims: Iterable[FinalClaim]) extends GameEvent

case object RestartingGame extends GameEvent