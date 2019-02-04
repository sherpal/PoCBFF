package components

import drawings.DrawablePath
import main.Main
import org.scalajs.dom
import org.scalajs.dom.html
import physics.pathfinding.graph.Vertex
import physics.pathfinding.immutablegraph.{AntoineGraph, Graph}
import physics.shape.{Segment, Shape}

object TestAStar extends Component {

  val name: String = "Test A*"

  val div: html.Div = dom.document.createElement("div").asInstanceOf[html.Div]

  private val setStart: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  setStart.textContent = "Set Start"
  private val setEnd: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  setEnd.textContent = "Set End"
  private val compute: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  compute.textContent = "Compute Path"

  div.appendChild(setStart)
  div.appendChild(setEnd)
  div.appendChild(compute)

  private var start: Option[Vertex] = None
  private var end: Option[Vertex] = None
  private var drawablePath: Option[DrawablePath] = None
  private var graph: Option[Graph] = None
  private var inflatedEdges: Vector[Segment] = Vector()

  private def destroyPath(): Unit = drawablePath match {
    case Some(p) => p.destroy()
    case None =>
  }

  private val shape: Shape = Shape.regularPolygon(4, 10)

  setStart.onclick = (_: dom.MouseEvent) => {
    destroyPath()
    start = None
    end = None
    Main.canvas.onclick = (_: dom.MouseEvent) => {
      val position = Main.mouseComplexPosition
      start = Some(new Vertex(position))
      Main.canvas.onclick = (_: dom.MouseEvent) => {}
    }
  }

  setEnd.onclick = (_: dom.MouseEvent) => {
    Main.canvas.onclick = (_: dom.MouseEvent) => {
      val position = Main.mouseComplexPosition
      end = Some(new Vertex(position))
      Main.canvas.onclick = (_: dom.MouseEvent) => {}
    }
  }

  compute.onclick = (_: dom.MouseEvent) => {
    if (start.isDefined && end.isDefined) {
      destroyPath()
      val startTime = new java.util.Date().getTime

      val currentGraph = new Graph(
        graph.get.vertices, List(start.get.position, end.get.position).foldLeft(graph.get.neighboursMap) {
          case (map, z) => AntoineGraph.addVertex(
            z, map, ObstacleMaker.quadTree, inflatedEdges
          )
        }
      )

      val path = currentGraph.a_*(
        start.get.position, end.get.position,
        (z1, z2) => !(z1 - z2)
      )

//      val path = GridAStar(
//        start.get.position, end.get.position,
//        (z1, z2) => 3 * !(z1 - z2),
//        (v: Complex) => !ObstacleMaker.quadTree.collides(shape, v, 0),
//        20
//      )

      println(s"It took ${new java.util.Date().getTime - startTime} ms to compute.")
      path match {
        case Some(p) =>
          println(s"Path length: ${p.length}")
          drawablePath = Some(new DrawablePath(p.toVector, cycle = false))
          drawablePath.get.layer = 10
        case None =>
          println("Could not find path")
          drawablePath = None
      }
    } else {
      println("Please set a start point and an end point.")
    }
  }

  def onLeave(): Unit = {
    Main.canvas.onclick = (_: dom.MouseEvent) => {}
    start = None
    end = None
  }

  def onEnter(): Unit = {
    graph = Some(AntoineGraph(ObstacleMaker.quadTree, shape.radius)._1)
    inflatedEdges = ObstacleMaker.quadTree.shapes.map(_.inflate(shape.radius * 0.9)).flatMap(_.edges).toVector
  }

  Component.registerComponent(this)


}
