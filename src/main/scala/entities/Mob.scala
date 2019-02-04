package entities

import components.{MovingMob, ObstacleMaker}
import drawings.DrawablePolygon
import physics.Complex
import physics.pathfinding.immutablegraph.{AntoineGraph, Graph}
import physics.shape.{Polygon, Segment, Shape}

final class Mob extends Body {

  val shape: Polygon = Shape.regularPolygon(4, 10)

  val drawableShape: DrawablePolygon = new DrawablePolygon(shape)

  var position: Complex = 0
  val speed: Double = 30

  var moving: Boolean = true

  var target: Option[Body] = None

  var rotation: Double = 0

  def findRotation(graph: Graph, inflatedEdges: Vector[Segment]): Unit = {
    target match {
      case Some(t) =>
//        val path = GridAStar(
//          position, t.position,
//          (z1, z2) => 3 * !(z1 - z2),
//          (v: Complex) => Main.worldBoundingBox.contains(v) & !ObstacleMaker.quadTree.collides(shape, v, rotation),
//          20
//        )

        val currentGraph = new Graph(
          graph.vertices, List(position, t.position).foldLeft(graph.neighboursMap) {
            case (map, z) => AntoineGraph.addVertex(
              z, map, ObstacleMaker.quadTree, inflatedEdges
            )
          }
        )

        currentGraph.a_*(
          position, t.position,
          (z1, z2) => !(z1 - z2)
        ) match {
          case Some(path) =>
            val dir = path.tail.head - position
            rotation = dir.arg
            moving = true
          case None =>
            moving = false
            rotation = 0
        }

      case None =>
        moving = false
        rotation = 0
    }
  }

  def move(delta: Double): Unit = if (moving) {
    position += Complex.rotation(rotation) * speed * delta / 1000
    drawableShape.rotation = rotation
    drawableShape.translation = position
  }

  def update(delta: Double): Unit = {
    findRotation(MovingMob.graph.get, MovingMob.inflatedEdges)
    move(delta)
  }

}
