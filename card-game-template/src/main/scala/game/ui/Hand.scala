package game.ui
import scala.collection.mutable

class Hand(mat: Playmat, xmin: Double, xmax: Double, mindx: Double, y: Double, z: Int) {
  val contents: mutable.ListBuffer[CardHandle] = mutable.ListBuffer()

  private def cardPosition(i: Int): (Double, Double, Int) = {
    val dx = Math.min((xmax - xmin) / contents.length, mindx)
    (xmin + dx * i, y, z)
  }
  def updateDisplay(): Unit = {
    contents.zipWithIndex.foreach { (h, i) =>
      val (cx, cy, cz) = cardPosition(i)
      h.move(cx, cy, cz + i)
    }
  }

  def give(c: CardHandle): Unit = {
    contents.append(c)
    updateDisplay()
  }
  def take(c: CardHandle): Unit = {
    contents.remove(contents.indexOf(c))
    updateDisplay()
  }

  def clear(): Unit = {
    contents.foreach { h => h.hide() }
    contents.clear()
  }
}
