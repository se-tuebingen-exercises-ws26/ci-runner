package game.ui

trait ListenerHandle {

  /**
   * Remove this event listener
   */
  def unregister(): Unit
}
trait ElementHandle {

  /**
   * Make it such that [[callback]] is called each time the element is clicked
   */
  def onClick(callback: () => Unit): ListenerHandle

  /**
   * Move the element to the given position and show it there if it is not yet visible
   *
   * @param z z-index of the element (where it is in the stacking order), must be >= 0
   * @param animationTime time to play the move animation for, in sec
   */
  def move(x: Double, y: Double, z: Int, animationTime: Double = 0.2): Unit

  /**
   * Hide the card from the playing area
   *
   * @param animationTime time to play the hide animation for, in sec
   */
  def hide(animationTime: Double = 0.2): Unit

  /**
   * Resize the element to the given width and height
   *
   * @param animationTime time to play the resize animation for, in sec
   */
  def resize(w: Double, h: Double, animationTime: Double = 0.2): Unit
}
trait CardHandle extends ElementHandle {

  /** Change the image displayed on the card */
  def setImage(path: String): Unit

  /**
   * Change the image displayed on the card, with a flip animation
   *
   * @param animationTime time to play the flip animation for, in sec
   */
  def flipImage(path: String, animationTime: Double = 0.2): Unit
}
trait TextHandle extends ElementHandle {

  /** Set the text content */
  def setText(text: String): Unit
}

trait Playmat {

  /**
   * Width of the playing area, is >= 1.0
   *
   * Either this or [[height]] is == 1.0. It might change during the run of the game.
   */
  def width: Double

  /**
   * Height of the playing area, is >= 1.0
   *
   * Either this or [[width]] is == 1.0. It might change during the run of the game.
   */
  def height: Double

  /**
   * Make a new Card
   * Note: It will not be shown until it is [[ElementHandle.move]]d somewhere
   */
  def mkCard(): CardHandle

  /**
   * Make a new Button
   * Note: It will not be shown until it is [[ElementHandle.move]]d somewhere
   *
   * @param text text displayed on the button
   */
  def mkButton(text: String): ElementHandle

  /**
   * Make a new text display
   * Note: It will not be shown until it is [[ElementHandle.move]]d somewhere
   *
   * @param initial initial text shown in the text display
   */
  def mkText(initial: String): TextHandle
}
