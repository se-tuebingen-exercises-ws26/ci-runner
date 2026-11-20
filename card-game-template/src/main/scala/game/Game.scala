package game
import ui.*
import org.scalajs.dom.{window, document, html}

// Note: This is not necessarily *nice* code. You will improve it in a later homework (not this one).

object Game {

  @main
  def main() = {
    val mat = new WebViewPlaymat()

    val dealer = new Dealer(mat)
    val player = new Player(mat, dealer)
    dealer.initialize()

  }
}
