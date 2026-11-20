package game
import scala.collection.mutable
import scala.util.Random

case class PhysicalCard(card: Card, view: ui.CardHandle) {
  var faceUp = false

  def flip(toBeFaceUp: Boolean = !faceUp): Unit = {
    faceUp = toBeFaceUp
    if (faceUp) {
      view.flipImage(ui.CardImages.front(card.suit, card.rank))
    } else {
      view.flipImage(ui.CardImages.back)
    }
  }
  def flipUp(): Unit = flip(true)
  def flipDown(): Unit = flip(false)
}

class PhysicalHand(val view: ui.Hand) {
  val contents: mutable.ListBuffer[PhysicalCard] = mutable.ListBuffer()

  def give(c: PhysicalCard): Unit = {
    contents.addOne(c)
    view.give(c.view)
  }
  def take(c: PhysicalCard): Unit = {
    contents.remove(contents.indexOf(c))
    view.take(c.view)
  }
  def clear(): Unit = {
    view.clear()
    contents.clear()
  }
}

class PhysicalDeck(val view: ui.CardStack) {
  private val contents: mutable.ArrayDeque[Card] = mutable.ArrayDeque()

  def initialize(): Unit = {
    contents.clear()
    view.clear()

    val stdDeck = mutable.ListBuffer[Card]()
    Suit.values.foreach { s =>
      Rank.values.foreach { r =>
        stdDeck.addOne(Card(r, s))
      }
    }

    contents.addAll(Random.shuffle(stdDeck))
    view.add(stdDeck.length)
  }

  def size(): Int = contents.length

  def drawCard(): PhysicalCard = {
    if (contents.isEmpty) initialize()
    val c = contents.removeHead()
    val v = view.drawTop()
    PhysicalCard(c, v)
  }

  def drawBottomCard(): PhysicalCard = {
    if (contents.isEmpty) initialize()
    val c = contents.removeLast()
    val v = view.drawBottom()
    PhysicalCard(c, v)
  }
}
