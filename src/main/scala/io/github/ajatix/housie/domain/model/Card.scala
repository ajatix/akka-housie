package io.github.ajatix.housie.domain.model

import io.github.ajatix.housie.common.Configs

import scala.util.Random

/**
  * Created by ajay on 25/03/17.
  */
class Card extends Serializable {

  private val range = Configs.range
  private val cardSize = Configs.cardSize
  private val cardGroup = Configs.cardGroup
  private val numbers: Array[Array[Int]] = Random.shuffle(1 to range).toArray.take(cardSize).sorted.grouped(cardGroup).toArray

  def display: Unit = numbers.foreach(row => println(row.map(n => "%2d".format(n)).mkString(" | ")))
  def get: Array[Array[Int]] = numbers
}
