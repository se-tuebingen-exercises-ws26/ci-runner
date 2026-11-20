package game
import scala.collection.mutable

class Dealer(mat: ui.Playmat) {
  val hand: PhysicalHand = new PhysicalHand(ui.Hand(mat, 0.3, 0.9, 0.1, 0.2, 0))
  val deck: PhysicalDeck = new PhysicalDeck(ui.CardStack(mat))

  val pointsDisplay = mat.mkText("?")
  val winnerDisplay = mat.mkText("")
  val players: mutable.ListBuffer[Player] = mutable.ListBuffer()
  var running = false

  val restartButton = mat.mkButton("Restart")
  restartButton.onClick(initialize)

  def initialize(): Unit = {
    restartButton.hide()
    winnerDisplay.hide()
    pointsDisplay.setText("?")

    hand.clear()

    running = true
    deck.view.move(0.05, 0.05, 0)
    if (deck.size() == 0) {
      deck.initialize()
    }
    pointsDisplay.move(0.95, 0.1, 0)

    players.foreach { p => p.initialize() }
    dealSelf()
    dealSelf()
  }

  def deal(): PhysicalCard = deck.drawCard()

  def handValue(): Int = {
    Blackjack.handValue(hand.contents.map { c => c.card }.toList)
  }

  def dealSelf(): Unit = {
    if (hand.contents.nonEmpty) {
      hand.contents.last.flipUp()
    }
    val c = deck.drawCard()
    hand.give(c)
  }
  def dealerPlay(): Unit = {
    while (handValue() < 17) {
      dealSelf()
    }
    endGame()
  }

  def endGame(): Unit = {
    if (hand.contents.nonEmpty) {
      hand.contents.last.flipUp()
    }
    pointsDisplay.setText(handValue().toString)
    players.foreach { p =>
      if (
        (p.handValue() > handValue() && p.handValue() <= 21)
        || (handValue() > 21 && p.handValue() <= 21)
      ) {
        winnerDisplay.setText("Player wins")
      } else {
        winnerDisplay.setText("Dealer wins")
      }
      running = false
      winnerDisplay.move(0.5, 0.5, 100)
      restartButton.move(0.5, 0.55, 100)
    }
  }
}

class Player(mat: ui.Playmat, dealer: Dealer) {
  dealer.players.append(this)

  val hand: PhysicalHand = new PhysicalHand(ui.Hand(mat, 0.2, 0.9, 0.1, 0.8, 0))

  val hitBtn = mat.mkButton("Hit")
  hitBtn.onClick(hit)
  val standBtn = mat.mkButton("Stand")
  standBtn.onClick(stand)
  val pointsDisplay = mat.mkText("0")
  pointsDisplay.move(0.95, 0.8, 0)

  def initialize(): Unit = {
    pointsDisplay.setText("0")
    hand.clear()
    hitBtn.move(0.05, 0.9, 0)
    standBtn.move(0.05, 0.95, 0)
    hit()
    hit()
  }

  def updateDisplay(): Unit = {
    pointsDisplay.setText(handValue().toString)
  }

  def handValue(): Int = {
    Blackjack.handValue(hand.contents.map { c => c.card }.toList)
  }

  def hit(): Unit = {
    if (dealer.running) {
      val c = dealer.deal()
      c.flipUp()
      hand.give(c)
    }
    updateDisplay()
  }

  def stand(): Unit = {
    hitBtn.hide()
    standBtn.hide()
    if (dealer.running) {
      dealer.dealerPlay()
    }
  }
}
