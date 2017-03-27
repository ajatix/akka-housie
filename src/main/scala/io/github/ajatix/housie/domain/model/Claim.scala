package io.github.ajatix.housie.domain.model

/**
  * Created by ajay on 25/03/17.
  */
case class Claim(id: String, player: String, prize: String, timestamp: Long) {
  def validate(): ValidatedClaim = ValidatedClaim(this, true, false)
  def invalidate(): ValidatedClaim = ValidatedClaim(this, false, false)
  def expire(): ValidatedClaim = ValidatedClaim(this, false, true)
}

case class ValidatedClaim(claim: Claim, valid: Boolean, expired: Boolean)

case class FinalClaim(playerName: String, prizeName: String)
