package game

enum Suit {
  case Spades
  case Diamonds
  case Clubs
  case Hearts
}

enum Rank {
  case Number(n: Int)
  case Jack, Queen, King, Ace

  require(
    this match {
      case Number(i) => i >= 2 && i <= 10
      case _         => true
    },
    "Card cannot have a number < 2 or > 10"
  )
}
object Rank {
  lazy val values: Array[Rank] = (2 to 10).map(Number(_)).toArray ++ List(Jack, Queen, King, Ace).toArray
  def valueOf(name: String): Rank = {
    name.toIntOption match {
      case None =>
        name match {
          case "Jack"  => Jack
          case "Queen" => Queen
          case "King"  => King
          case "Ace"   => Ace
          case _       => throw new IllegalArgumentException(s"Rank has no case for ${name}")
        }
      case Some(n) if n >= 2 && n <= 10 => Number(n)
      case Some(n)                      => throw new IllegalArgumentException(s"Rank has no case for Number(${n})")
    }
  }

}
case class Card(rank: Rank, suit: Suit)

object Blackjack {
  def value(r: Rank): Int = r match {
    case Rank.Number(i)                     => i
    case Rank.Ace                           => 11
    case Rank.Jack | Rank.Queen | Rank.King => 10
  }

  def handValue(cards: List[Card]): Int = {
    var total = cards.map { c => value(c.rank) }.sum
    var aces = cards.count { c => c.rank == Rank.Ace }

    while (total > 21 && aces > 0) {
      total -= 10
      aces -= 1
    }
    total
  }
}
