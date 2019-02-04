package components
import drawings.{DrawableDisk, DrawablePath, DrawablePolygon}
import main.Main
import org.scalajs.dom
import org.scalajs.dom.html
import physics.Complex
import physics.pathfinding.immutablegraph.{AntoineGraph, CedricsGraph}
import physics.quadtree.ShapeQT
import physics.shape.{Polygon, Shape}

import scala.collection.mutable

object ObstacleMaker extends Component {

  private implicit class ComplexToDot(z: Complex) {

    def dot: DrawablePolygon = {
      val polygon = new DrawablePolygon(
        Shape.translatedRegularPolygon(4, 3, z)
      )
      polygon.color = "red"
      polygon
    }
  }

  val name: String = "Obstacle Maker"

  private val _obstacles: mutable.Set[DrawablePolygon] = mutable.Set()
  private var _quadTree: ShapeQT = ShapeQT(Main.worldBoundingBox)
  private var _graphVertices: Vector[DrawablePolygon] = Vector()
  private var _allEdges: Vector[DrawablePath] = Vector()

  private def clearGraphVertices(): Unit = {
    _graphVertices.foreach(_.destroy())
    _graphVertices = Vector()
  }

  private def makeCedricsGraph(): Unit = {
    clearGraphVertices()
    //_graphVertices = CedricsGraph(Main.worldBoundingBox, _quadTree.shapes).vertices.map(_.dot)
    val (graph, allEdges, _) = AntoineGraph(_quadTree, 10.0)

    _graphVertices = graph.vertices.map(_.dot)

    _allEdges.foreach(_.destroy())

    _allEdges = allEdges.map({
      case (v1, v2) =>
        val drawablePath: DrawablePath = new DrawablePath(Vector(v1, v2), cycle = false)
        drawablePath.color = "green"
        drawablePath
    }).toVector


  }

  def quadTree: ShapeQT = _quadTree

  val div: html.Div = dom.document.createElement("div").asInstanceOf[html.Div]

  private val newObstacle: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  newObstacle.textContent = "New Obstacle"
  private val done: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  done.textContent = "Done"
  private val cancel: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  cancel.textContent = "Cancel"
  private val clear: html.Button = dom.document.createElement("button").asInstanceOf[html.Button]
  clear.textContent = "Clear Obstacles"


  div.appendChild(newObstacle)
  div.appendChild(cancel)
  div.appendChild(done)
  div.appendChild(clear)

  done.disabled = true
  cancel.disabled = true

  private var constructedVertices: List[Complex] = Nil
  private var shapes: List[DrawableDisk] = Nil
  private var shape: Option[DrawablePath] = None

  private var constructing: Boolean = false

  private def start(): Unit = {
    newObstacle.disabled = true
    cancel.disabled = false
    done.disabled = false
    constructing = true
    Main.canvas.onclick = (_: dom.MouseEvent) => {
      val vertex = Main.mouseComplexPosition
      constructedVertices :+= vertex
      shapes +:= new DrawableDisk(vertex, 3)

      if (shape.isDefined)
        shape.get.destroy()
      if (constructedVertices.tail.nonEmpty) {
        shape = Some(new DrawablePath(constructedVertices.toVector, cycle = true))
      }
    }
  }

  private def finished(): Unit = {
    if (constructedVertices.length >= 3) {
      val obstacle = Polygon(constructedVertices.toVector)
      _obstacles += new DrawablePolygon(obstacle)
      val inflated = new DrawablePolygon(obstacle.inflate(10))
      inflated.layer = -1
      inflated.color = "blue"
      _quadTree = _quadTree :+ obstacle
      makeCedricsGraph()
    } else {
      dom.console.warn("A polygon must have at least 3 vertices")
    }
    stop()
  }

  private def stop(): Unit = {
    newObstacle.disabled = false
    done.disabled = true
    cancel.disabled = true

    Main.canvas.onclick = (_: dom.MouseEvent) => {}

    shapes.foreach(_.destroy())
    if (shape.isDefined)
      shape.get.destroy()
    shape = None
    shapes = Nil
    constructedVertices = Nil
    constructing = false
  }

  private def clearObstacles(): Unit = {
    _obstacles.foreach(_.destroy())
    _obstacles.clear()
    _quadTree = ShapeQT(Main.worldBoundingBox)
    clearGraphVertices()

  }

  newObstacle.onclick = (_: dom.MouseEvent) => start()
  cancel.onclick = (_: dom.MouseEvent) => stop()
  done.onclick = (_: dom.MouseEvent) => finished()
  clear.onclick = (_: dom.MouseEvent) => clearObstacles()

  def onLeave(): Unit = stop()

  def onEnter(): Unit = {}

  Component.registerComponent(this)

}