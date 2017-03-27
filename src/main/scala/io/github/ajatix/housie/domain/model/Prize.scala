package io.github.ajatix.housie.domain.model

/**
  * Created by ajay on 25/03/17.
  */
trait Prize {
  val name: String
  def validate(card: Card, state: Set[Tile]): Boolean
}

case object FULL_HOUSE extends Prize {

  val name = "full_house"
  def validate(card: Card, state: Set[Tile]): Boolean = {
    val numbers = card.get.toSet.flatten
    numbers.diff(state.map(tile => tile.number)).isEmpty
  }

}

case class ROW(idx: Int) extends Prize {
  require(idx > 0)

  val name = s"row_$idx"
  def validate(card: Card, state: Set[Tile]): Boolean = {
    val numbers = card.get.apply(idx - 1).toSet
    numbers.diff(state.map(tile => tile.number)).isEmpty
  }

}

case class FAST(num: Int) extends Prize {
  require(num > 0)

  val name = s"fast_$num"
  def validate(card: Card, state: Set[Tile]): Boolean = {
    val numbers = card.get.toSet.flatten
    state.map(tile => tile.number).intersect(numbers).size >= num
  }

}
