package components

import drawings.DrawablePolygon
import main.Main
import org.scalajs.dom
import org.scalajs.dom.html
import physics.shape.Shape

object FollowTheMouse extends Component {

  val name: String = "Follow the Mouse"

  val div: html.Div = dom.document.createElement("div").asInstanceOf[html.Div]

  private val startButton: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  startButton.textContent = "Start"
  private val endButton: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  endButton.textContent = "End"

  div.appendChild(startButton)
  div.appendChild(endButton)

  private val mouseFollower: DrawablePolygon = new DrawablePolygon(Shape.regularPolygon(10, 15))
  mouseFollower.destroy()

  def update(): Unit = {
    mouseFollower.translation = Main.mouseComplexPosition
    mouseFollower.color =
      if (ObstacleMaker.quadTree.collides(mouseFollower.shape, mouseFollower.translation, 0)) "red"
      else "white"
  }

  startButton.onclick = (_: dom.MouseEvent) => mouseFollower.reset()
  endButton.onclick = (_: dom.MouseEvent) => mouseFollower.destroy()

  override def onLeave(): Unit = mouseFollower.destroy()

  def onEnter(): Unit = {}

  Component.registerComponent(this)

}
