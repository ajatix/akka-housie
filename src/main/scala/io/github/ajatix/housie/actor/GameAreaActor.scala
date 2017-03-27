package io.github.ajatix.housie.actor

import akka.actor.{Actor, ActorRef, FSM, PoisonPill, Props}
import akka.persistence._
import akka.persistence.fsm.PersistentFSM
import io.github.ajatix.housie.actor.GameBoardActor.NextTile
import io.github.ajatix.housie.common.Helpers
import io.github.ajatix.housie.domain._
import io.github.ajatix.housie.domain.model._

import scala.concurrent.duration._
import scala.reflect.{ClassTag, classTag}

/**
  * Created by ajay on 24/03/17.
  */
class GameRoomActor extends PersistentFSM[GameState, GameData, GameEvent] {

  override def persistenceId = context.self.path.name

  override def domainEventClassTag = classTag[GameEvent]

  override def recovery = {
    Recovery(fromSnapshot = SnapshotSelectionCriteria.Latest)
  }

  startWith(Registering, GameRoomData.empty)

  when(Registering) {
    case Event(PlayersReady, data) =>
      goto(Readying) applying GenerateCards andThen {
        case _ =>
          notify({
            player: PlayerWithActor =>
              val card = stateData.cards(player.name)
              player.actor ! PlayerCard(card)
          })
      }
  }

  when(Readying) {
    case Event(StartGame, data) =>
      goto(Running)
  }

  when(Running) {
    case Event(GetNextTile, data) =>
      context.actorSelection("board") ! NextTile
      stay
    case Event(tile: Tile, data) =>
      broadcast(TileGenerated(tile))
      stay applying AddTile(tile)
    case Event(TilesOver, data) =>
      goto(Reviewing)
    case Event(EndGame, data) =>
      goto(Reviewing)
    case Event(PlayerClaimRequest(id, playerName, prizeName), data) =>
      stay applying ProcessClaim(Claim(id, playerName, prizeName, Helpers.now)) andThen {
        case _ =>
          stateData.claims.get(id).foreach {
            case ValidatedClaim(claim, _, true) =>
              reply(claim.player, PrizeAlreadyClaimed)
            case ValidatedClaim(claim, true, _) =>
              broadcast(PrizeClaimed(claim.prize, claim.player))
            case ValidatedClaim(claim, false, _) =>
              reply(claim.player, PrizeClaimInvalid)
          }
      }
  }

  when(Reviewing) {
    case Event(RestartGame, data) =>
      goto(Readying) applying RestartingGame
  }

  whenUnhandled {
    case Event(PlayerJoining(player, actor), data) =>
      stay applying PlayerAdded(player.attachActor(actor)) andThen {
        case _ =>
          reply(player.name, PlayersInRoom(data.players.values.map(p => p.name)))
          broadcast(PlayerJoined(player.name))
      }
    case Event(PlayerQuitting(playerName), data) =>
      stay applying PlayerRemoved(playerName) andThen {
        case _ =>
          broadcast(PlayerQuit(playerName))
      }
    case Event(RecoveryCompleted, _) =>
      log.info("recovery completed")
      stay
    case Event(SaveSnapshotSuccess(metadata), _) =>
      log.info("snapshot has been saved")
      stay
  }

  onTransition {
    case Registering -> Readying =>
      saveStateSnapshot()
    case Readying -> Running =>
      saveStateSnapshot()
      val board = context.actorOf(Props[GameBoardActor], "board")
      setTimer("board", GetNextTile, 1 seconds, repeat = true)
    case Running -> Reviewing =>
      saveStateSnapshot()
      cancelTimer("board")
      context.actorSelection("board") ! PoisonPill
      val finalClaims = stateData.claims.values.filter(c => c.valid).map(c => FinalClaim(c.claim.player, c.claim.prize))
      broadcast(DisplayWinners(finalClaims))
  }

  override def applyEvent(domainEvent: GameEvent, currentData: GameData) = domainEvent match {
    case PlayerAdded(player) =>
      currentData.players.get(player.name) match {
        case None => currentData.addPlayer(player)
        case Some(_) => currentData.updatePlayer(player)
      }
    case PlayerRemoved(playerName) =>
      currentData.removePlayer(playerName)
    case GenerateCards =>
      currentData.generateCards()
    case AddTile(tile) =>
      currentData.addTile(tile)
    case ProcessClaim(claim) =>
      currentData.validateClaim(claim)
    case RestartingGame =>
      currentData.restart()
  }

  def reply(playerName: String, event: GameEvent): Unit = {
    stateData.players.get(playerName).foreach(player => player.actor ! event)
  }

  def broadcast(event: GameEvent): Unit = {
    stateData.players.values.foreach(player => player.actor ! event)
  }

  def notify(f: PlayerWithActor => Unit): Unit = {
    stateData.players.values.foreach(player => f(player))
  }

}