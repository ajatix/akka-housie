package io.github.ajatix.housie.service

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import io.github.ajatix.housie.actor.GameRoomActor
import io.github.ajatix.housie.common.Helpers
import io.github.ajatix.housie.domain._
import io.github.ajatix.housie.common.Helpers._
import io.github.ajatix.housie.domain.model.{Card, Player, Prize, Tile}

/**
  * Created by ajay on 25/03/17.
  */
class GameService(implicit val actorSystem: ActorSystem, implicit val actorMaterializer: ActorMaterializer) extends Directives {

  val webSocketRoute = get {
    path("join") {
      parameter("name") { playerName =>
        handleWebSocketMessages(flow(playerName))
      }
    }
  }

  val gameRoomActor = actorSystem.actorOf(Props[GameRoomActor], randomize("room"))
  val playerActorSource = Source.actorRef[GameEvent](5, OverflowStrategy.fail)

  def flow(playerName: String): Flow[Message, Message, Any] = Flow.fromGraph(GraphDSL.create(playerActorSource) { implicit builder => playerActor =>
    import GraphDSL.Implicits._

    val materialization = builder.materializedValue.map(playerActorRef => PlayerJoining(Player(playerName), playerActorRef))
    val merge = builder.add(Merge[GameEvent](2))

    val messagesToGameEventsFlow = builder.add(Flow[Message].map {
      case TextMessage.Strict(msg) => msg match {
        case readyMessage() => PlayersReady
        case startMessage() => StartGame
        case stopMessage() => StopGame
        case quitMessage() => PlayerQuitting(playerName)
        case claimMessage(prizeName) => PlayerClaimRequest(Helpers.id, playerName, prizeName)
        case restartMessage() => RestartGame
      }
    })

    val gameEventsToMessagesFlow = builder.add(Flow[GameEvent].map {
      case PlayersInRoom(players) =>
        TextMessage(s"Players already in room: [${players.mkString}]")
      case PlayerJoined(playerName) =>
        TextMessage(s"Player $playerName has joined the room")
      case PlayerRemoved(playerName) =>
        TextMessage(s"Player $playerName has left the room")
      case PlayerCard(card) =>
        TextMessage(s"Your card: ${card.get.map(row => row.mkString(",")).mkString("|")}")
      case PrizeAlreadyClaimed =>
        TextMessage("Sorry! the prize has already been claimed")
      case PrizeClaimInvalid =>
        TextMessage("Hey! No cheating, your claim is invalid")
      case PrizeClaimed(prize, player) =>
        TextMessage(s"Kudos! $player just claimed $prize")
      case TileGenerated(tile) =>
        TextMessage(s"Tile: ${tile.number}")
      case DisplayWinners(claims) =>
        TextMessage(s"Final winners: [${claims.map(c => s"${c.playerName}: ${c.prizeName}").mkString("|")}]")
      case PlayerQuit(playerName) =>
        TextMessage(s"Player [$playerName] has left the room")
    })

    val gameAreaActorSink = Sink.actorRef[GameEvent](gameRoomActor, PlayerQuitting(playerName))

    materialization ~> merge ~> gameAreaActorSink
    messagesToGameEventsFlow ~> merge

    playerActor ~> gameEventsToMessagesFlow

    FlowShape(messagesToGameEventsFlow.in, gameEventsToMessagesFlow.out)
  })

}
