package game.ui
import scala.collection.mutable

class CardStack(mat: Playmat) extends ElementHandle {
  private val cards: mutable.ArrayDeque[CardHandle] = mutable.ArrayDeque()
  private var topListener: ListenerHandle = null
  private var n: Int = 0

  // Current position
  private var x: Double = 0
  private var y: Double = 0
  private var z: Int = 0
  private var visible: Boolean = false
  private val VISIBLE_SIZE = 10

  private def cardPosition(i: Int): (Double, Double, Int) =
    (x + 0.002 * i, y + 0.002 * i, z + i)

  private def setTopListener(tl: ListenerHandle): Unit = {
    if (topListener != null) {
      topListener.unregister()
    }
    topListener = tl
  }
  def add(): Unit = {
    n = n + 1
    if (n <= VISIBLE_SIZE) {
      val c = mat.mkCard()
      val (cx, cy, cz) = cardPosition(cards.length)
      if (visible) {
        c.move(cx, cy, cz)
      }
      setTopListener(c.onClick(clicked))
      cards.addOne(c)
    }
  }
  def add(k: Int): Unit = {
    if (k > 0) {
      n = n + 1
      var i = 1
      while (n <= VISIBLE_SIZE) {
        n = n + 1
        i = i + 1
        val c = mat.mkCard()
        val (cx, cy, cz) = cardPosition(cards.length)
        if (visible) {
          c.move(cx, cy, cz)
        }
        cards.addOne(c)
      }
      setTopListener(cards.last.onClick(clicked))
      n = n + k - i
    }
  }
  def putTop(c: CardHandle) = {
    n = n + 1
    val (cx, cy, cz) = cardPosition(cards.length)
    setTopListener(c.onClick(clicked))
    cards.addOne(c)
    c.move(cx, cy, cz)
  }
  def drawTop(): CardHandle = {
    n = n - 1
    val c = cards.removeLast()
    if (n >= VISIBLE_SIZE) {
      val rc = mat.mkCard()
      if (visible) {
        val (cx, cy, cz) = cardPosition(cards.length)
        rc.move(cx, cy, cz, 0)
      }
      cards.addOne(rc)
    }
    setTopListener(cards.lastOption.map(_.onClick(clicked)).orNull)
    c
  }
  def drawBottom(): CardHandle = {
    n = n - 1
    val c = cards.removeHead()
    if (n >= VISIBLE_SIZE) {
      val rc = mat.mkCard()
      if (visible) {
        val (cx, cy, cz) = cardPosition(0)
        rc.move(cx, cy, cz, 0)
      }
      cards.insert(0, rc)
    }
    if (n == 0) {
      setTopListener(null)
    }
    c
  }

  def clear(): Unit = {
    n = 0
    cards.foreach { h => h.hide() }
    setTopListener(null)
    cards.clear()
  }

  override def move(x: Double, y: Double, z: Int, animationTime: Double): Unit = {
    this.x = x; this.y = y; this.z = z
    this.visible = true
    cards.zipWithIndex.foreach { case (h, i) =>
      val (cx, cy, cz) = cardPosition(i)
      h.move(cx, cy, cz, animationTime)
    }
  }

  override def hide(animationTime: Double): Unit = {
    this.visible = false
    cards.foreach { c => c.hide(animationTime) }
  }

  override def resize(w: Double, h: Double, animationTime: Double): Unit = {
    cards.foreach { c => c.resize(w, h, animationTime) }
  }

  private val clickListeners: mutable.Set[() => Unit] = mutable.Set()
  private def clicked(): Unit = clickListeners.foreach { l => l() }
  override def onClick(callback: () => Unit): ListenerHandle = {
    clickListeners.addOne(callback)
    new ListenerHandle {
      override def unregister(): Unit = {
        clickListeners.remove(callback)
      }
    }
  }
}
