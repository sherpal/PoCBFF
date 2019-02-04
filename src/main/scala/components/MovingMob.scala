package components

import entities.Mob
import main.Main
import org.scalajs.dom
import org.scalajs.dom.html
import physics.pathfinding.immutablegraph.{AntoineGraph, Graph}
import physics.shape.Segment

object MovingMob extends Component {

  val name: String = "Moving Mob"

  val mob: Mob = new Mob
  mob.moving = false
  mob.drawableShape.destroy()
  val target: Mob = new Mob
  target.moving = false
  target.drawableShape.destroy()


  def onLeave(): Unit = {
    mob.drawableShape.destroy()
    target.drawableShape.destroy()
    mob.target = None
  }

  var graph: Option[Graph] = None
  var inflatedEdges: Vector[Segment] = Vector()

  def onEnter(): Unit = {
    graph = Some(AntoineGraph(ObstacleMaker.quadTree, mob.shape.radius)._1)
    inflatedEdges = ObstacleMaker.quadTree.shapes.map(_.inflate(mob.shape.radius * 0.9)).flatMap(_.edges).toVector
  }



  val div: html.Div = dom.document.createElement("div").asInstanceOf[html.Div]

  private val start: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  start.textContent = "Start"

  div.appendChild(start)


  start.onclick = (_: dom.MouseEvent) => {
    mob.target = Some(target)
    mob.drawableShape.reset()
    target.drawableShape.reset()
  }

  def update(delta: Double): Unit = {

    if (mob.target.isDefined) {
      target.position = Main.mouseComplexPosition
      target.drawableShape.translation = target.position
      mob.update(delta)
    }

  }


  Component.registerComponent(this)
}
