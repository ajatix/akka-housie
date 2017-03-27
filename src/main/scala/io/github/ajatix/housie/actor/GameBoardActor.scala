package io.github.ajatix.housie.actor

import akka.persistence.{PersistentActor, SnapshotOffer}
import io.github.ajatix.housie.actor.GameBoardActor.{BoardEvent, NextTile}
import io.github.ajatix.housie.domain.model.Board

/**
  * Created by ajay on 25/03/17.
  */
class GameBoardActor extends PersistentActor {

  override def persistenceId = context.self.path.name

  var board = new Board

  def updateBoard(evt: BoardEvent): Unit = {
    board = board.update(evt.num)
  }

  val receiveRecover: Receive = {
    case evt: BoardEvent =>
      println(evt.idx)
      updateBoard(evt)
    case SnapshotOffer(_, snapshot: Board) =>
      board = snapshot
  }

  val receiveCommand: Receive = {
    case NextTile =>
      board.next match {
        case Left(msg) =>
          sender ! msg
          deleteMessages(board.range)
        case Right(tile) =>
          persist(BoardEvent(board.index, tile.number))(updateBoard)
          sender ! tile
      }
  }

}

object GameBoardActor {

  case object NextTile
  case class BoardEvent(idx: Int, num: Int)

}