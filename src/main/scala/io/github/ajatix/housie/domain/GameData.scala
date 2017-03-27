package io.github.ajatix.housie.domain

import akka.actor.ActorRef
import io.github.ajatix.housie.domain.model._

import scala.util.Random

/**
  * Created by ajay on 24/03/17.
  */
trait GameData2 {
  val players: Seq[PlayerWithActor]
  val tiles: Seq[Tile]
  val claims: Seq[Claim]
  val prizes: Set[Prize]
  def addPlayer(player: PlayerWithActor): GameData
  def updatePlayer(player: PlayerWithActor): GameData
  def addTile(tile: Tile): GameData
  def addClaim(claim: Claim): GameData
  def reset: GameData
}

trait GameData {
  val players: Map[String, PlayerWithActor]
  val cards: Map[String, Card]
  val tiles: List[Tile]
  val prizes: Set[Prize]
  val claims: Map[String, ValidatedClaim]

  def addPlayer(player: PlayerWithActor): GameData
  def removePlayer(playerName: String): GameData
  def updatePlayer(player: PlayerWithActor): GameData

  def generateCards(): GameData

  def addTile(tile: Tile): GameData

  def validateClaim(claim: Claim): GameData

  def restart(): GameData

  def reset(): GameData
}

case class GameRoomData(players: Map[String, PlayerWithActor], cards: Map[String, Card], tiles: List[Tile], prizes: Set[Prize], claims: Map[String, ValidatedClaim]) extends GameData {

  def addPlayer(player: PlayerWithActor): GameData = {
    copy(players = players + (player.name -> player))
  }
  def removePlayer(playerName: String): GameData = {
    copy(players = players - playerName)
  }
  def updatePlayer(player: PlayerWithActor): GameData = {
    copy(players = players + (player.name -> player))
  }

  def generateCards(): GameData = {
    copy(cards = {
      players.keys.map(player => player -> (cards.get(player) match {
        case None => new Card
        case Some(c) => c
      })).toMap
    })
  }

  def addTile(tile: Tile): GameData = {
    copy(tiles = tiles :+ tile)
  }

  def validateClaim(claim: Claim): GameData = {
    prizes.find(p => p.name == claim.prize) match {
      case None =>
        copy(claims = claims + (claim.id -> claim.expire()))
      case Some(p) => if (p.validate(cards(claim.player), tiles.toSet)) {
        copy(prizes = prizes - p, claims = claims + (claim.id -> claim.validate()))
      } else {
        copy(claims = claims + (claim.id -> claim.invalidate()))
      }
    }
  }

  def restart(): GameData = {
    copy(cards = Map.empty[String, Card], tiles  = List.empty[Tile], prizes = GameRoomData.defaultPrizes, claims = Map.empty[String, ValidatedClaim])
  }

  def reset(): GameData = {
    GameRoomData.empty
  }
}

object GameRoomData {

  val defaultPrizes: Set[Prize] = Set(
    FAST(5),
    ROW(1),
    ROW(2),
    ROW(3),
    FULL_HOUSE
  )

  def empty = GameRoomData(Map.empty[String, PlayerWithActor], Map.empty[String, Card], List.empty[Tile], defaultPrizes, Map.empty[String, ValidatedClaim])

}

//case object NewGameData extends GameData {
//  val players: Seq[PlayerWithActor] = Nil
//  val tiles: Seq[Tile] = Nil
//  val claims: Seq[Claim] = Nil
//  val prizes: Set[Prize] = Set(
//    FAST(5),
//    ROW(1),
//    ROW(2),
//    ROW(3),
//    FULL_HOUSE
//  )
//
//  def addPlayer(player: PlayerWithActor) =
//    RunningGameData(player +: players, tiles, claims, prizes)
//  def updatePlayer(player: PlayerWithActor) = addPlayer(player)
//  def addTile(tile: Tile) =
//    RunningGameData(players, tile +: tiles, claims, prizes)
//  def addClaim(claim: Claim) =
//    RunningGameData(players, tiles, claim +: claims, prizes)
//  def reset =
//    NewGameData
//
//}
//
//case class RunningGameData(players: Seq[PlayerWithActor], tiles: Seq[Tile], claims: Seq[Claim], prizes: Set[Prize]) extends GameData {
//  def addPlayer(player: PlayerWithActor) =
//    RunningGameData(players :+ player, tiles, claims, prizes)
//  def updatePlayer(player: PlayerWithActor) = {
//    val nPlayers = players.filter(p => p.player.name == player.player.name).map(p => p.copy(actor = player.actor))
//    RunningGameData(nPlayers, tiles, claims, prizes)
//  }
//  def addTile(tile: Tile) =
//    RunningGameData(players, tiles :+ tile, claims, prizes)
//  def addClaim(claim: Claim) =
//    RunningGameData(players, tiles, claims :+ claim, prizes)
//  def reset =
//    NewGameData
//}