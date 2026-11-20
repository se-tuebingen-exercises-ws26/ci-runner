package game.ui

// NOTE FOR STUDENTS: THIS FILE CONTAINS THE ACTUAL UI IMPLEMENTATION.
// YOU DO NOT HAVE TO UNDERSTAND OR READ IT.
// INSTEAD, PROGRAM AGAINST THE INTERFACE IN Playmat.scala

import org.scalajs.dom.{document, html, window}
import org.scalajs.dom.Event

extension (self: html.Element)
  def addEventListenerOnce[T <: Event](`type`: String, listener: Function1[T, ?]): Unit = {
    var wlistener: Function1[T, ?] = { _ => () }
    wlistener = { (a: T) =>
      listener(a)
      self.removeEventListener(`type`, wlistener)
    }
    self.addEventListener(`type`, wlistener)
  }

extension (self: html.Element)
  def afterTransition(animationTime: Double, callback: () => Unit): Unit = {
    if (animationTime < 0.0001) {
      callback()
    } else {
      self.addEventListenerOnce("transitionend", _ => callback())
    }
  }

class WebViewPlaymat extends Playmat {
  val contentDiv = document.querySelector("#content").asInstanceOf[html.Element]

  def width = Math.max(contentDiv.clientWidth / contentDiv.clientHeight, 1.0)
  def height = Math.max(contentDiv.clientHeight / contentDiv.clientWidth, 1.0)

  override def mkCard() = new WebViewCardHandle(this)
  override def mkButton(text: String) = new WebViewButton(text, this)
  override def mkText(initial: String): TextHandle = new WebViewText(initial, this)
}

abstract class WebViewHandle(parent: WebViewPlaymat) extends ElementHandle {
  def element: html.Div
  private var hidden = true
  override def onClick(callback: () => Unit): ListenerHandle = {
    val handler: scala.scalajs.js.Function1[Event, ?] = { _ => callback() }
    element.addEventListener("click", handler)
    return new ListenerHandle {
      override def unregister(): Unit = {
        element.removeEventListener("click", handler)
      }
    }
  }

  override def move(x: Double, y: Double, z: Int, animationTime: Double = 0.2): Unit = {
    hidden = false
    if (!parent.contentDiv.children.contains(element)) {
      parent.contentDiv.appendChild(element)
    }
    window.setTimeout(
      { () =>
        element.style.transition =
          s"left ${animationTime}s, top ${animationTime}s, z-index ${animationTime}s, opacity ${animationTime}s"
        element.style.setProperty("opacity", "1")
        element.style.setProperty("left", s"${x * 100}dvmin")
        element.style.setProperty("top", s"${y * 100}dvmin")
        element.style.zIndex = z.toString
        element.afterTransition(
          animationTime,
          () =>
            element.style.transition = ""
        )
      },
      0
    )
  }

  override def hide(animationTime: Double = 0.2): Unit = {
    hidden = true
    if (hidden) {
      if (parent.contentDiv.children.contains(element)) {
        parent.contentDiv.removeChild(element)
      }
    } else {
      window.setTimeout(
        { () =>
          element.style.transition = s"opacity ${animationTime}s"
          element.style.opacity = "0"
          element.afterTransition(
            animationTime,
            () =>
              if (parent.contentDiv.children.contains(element) && hidden) {
                parent.contentDiv.removeChild(element)
              }
          )
        },
        0
      )
    }
  }

  override def resize(w: Double, h: Double, animationTime: Double = 0.2): Unit = {
    window.setTimeout(
      { () =>
        element.style.transition = s"width ${animationTime}s, height ${animationTime}s"
        element.style.width = s"${w * 100}dvmin"
        element.style.height = s"${h * 100}dvmin"
        element.afterTransition(animationTime, () => element.style.transition = "")
      },
      0
    )
  }
}
class WebViewText(str: String, playmat: WebViewPlaymat) extends WebViewHandle(playmat) with TextHandle {
  val element = document.createElement("div").asInstanceOf[html.Div]
  element.innerText = str
  element.classList.add("text")

  override def setText(text: String): Unit = {
    element.innerText = text
  }
}
class WebViewButton(text: String, parent: WebViewPlaymat) extends WebViewHandle(parent) {
  val element = document.createElement("div").asInstanceOf[html.Div]
  element.classList.add("button")
  val button = document.createElement("button").asInstanceOf[html.Button]
  button.innerText = text
  element.appendChild(button)
}
class WebViewCardHandle(parent: WebViewPlaymat) extends WebViewHandle(parent) with CardHandle {
  val element = document.createElement("div").asInstanceOf[html.Div]
  element.classList.add("card")
  element.style.width = "17.5dvmin"
  element.style.height = "25dvmin"

  var imgEl = document.createElement("img").asInstanceOf[html.Image]
  imgEl.setAttribute("src", CardImages.back)
  imgEl.classList.add("card-img")
  element.appendChild(imgEl)

  override def setImage(path: String): Unit = {
    imgEl.setAttribute("src", path)
  }
  override def flipImage(path: String, animationTime: Double = 0.2): Unit = {
    val frontImage = imgEl
    val backImage = document.createElement("img").asInstanceOf[html.Image]
    backImage.setAttribute("src", path)
    val inner = document.createElement("div").asInstanceOf[html.Div]

    element.classList.add("flip-card-outer")

    inner.classList.add("flip-card-inner")
    inner.style.transition = s"transform ${animationTime}s"

    frontImage.classList.add("flip-card-front")
    backImage.classList.add("flip-card-back")
    backImage.classList.add("card-img")

    element.children.foreach(element.removeChild)
    inner.appendChild(frontImage)
    inner.appendChild(backImage)
    element.appendChild(inner)

    inner.afterTransition(
      animationTime,
      { () =>
        backImage.classList.remove("flip-card-back")
        element.removeChild(inner)
        element.classList.remove("flip-card-outer")
        element.classList.remove("flipped")
        element.appendChild(backImage)
        imgEl = backImage
      }
    )
    window.setTimeout(
      () =>
        element.classList.add("flipped"),
      0
    )
  }
}
