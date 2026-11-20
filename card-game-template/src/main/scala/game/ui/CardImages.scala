package game
package ui

/**
 * Helper to get paths to the common card face images
 */
object CardImages {

  /** Path to a card backside image */
  val back = "./cardback/Tiled_blue_broken_line_rhombus_card_back.svg"

  def suitname(suit: Suit): String = suit match {
    case Suit.Diamonds => "diamonds"
    case Suit.Clubs    => "clubs"
    case Suit.Hearts   => "hearts"
    case Suit.Spades   => "spades"
  }
  def rankname(rank: Rank): String = rank match {
    case Rank.Number(n) => s"${n}"
    case Rank.Jack      => "jack"
    case Rank.Queen     => "queen"
    case Rank.King      => "king"
    case Rank.Ace       => "ace"
  }

  /** Get the path for the image of a given card front side */
  def front(suit: Suit, rank: Rank): String = s"./svgcards/${rankname(rank)}_of_${suitname(suit)}.svg"
}
