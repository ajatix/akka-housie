package io.github.ajatix.housie.domain.model

import io.github.ajatix.housie.common.{Configs, Helpers}
import io.github.ajatix.housie.domain.{GameEvent, TilesOver}

import scala.util.Random

/**
  * Created by ajay on 25/03/17.
  */
class Board extends Serializable {

  val range = Configs.range
  private var numbers = Random.shuffle(1 to range).toList

  def update(number: Int): Board = {
    this.numbers = this.numbers diff List(number)
    this
  }

  def index: Int = {
    range - numbers.size
  }

  def next: Either[GameEvent, Tile] = numbers.headOption match {
    case None => Left(TilesOver)
    case Some(number) => Right(Tile(number, Helpers.now))
  }

}

