package zombies.entities

import physics.Complex
import physics.pathfinding.immutablegraph.{AntoineGraph, Graph}
import physics.quadtree.ShapeQT
import physics.shape.{Polygon, Segment, Shape}

final class Zombie(
                    val id: Long,
                    val time: Long,
                    val pos: Complex,
                    val direction: Double,
                    val moving: Boolean
                  ) extends MovingBody {

  val speed: Double = Zombie.zombieSpeed
  val rotation: Double = direction

  val shape: Polygon = Zombie.zombieShape

}

object Zombie {

  val zombieSpeed: Double = 75

  val zombieRadius: Double = 10

  val zombieShape: Polygon = Shape.regularPolygon(3, zombieRadius)

  def updateZombies(
                     time: Long,
                     zombies: List[Zombie],
                     target: Complex,
                     graph: Graph,
                     quadTree: ShapeQT,
                     inflatedEdges: List[Segment]
                   ): List[Zombie] = {
    val newGraph = new Graph(
      target +: graph.vertices, AntoineGraph.addVertex(
        target, graph.neighboursMap, quadTree, inflatedEdges,
        forceInclusion = true
      )
    )
    zombies.map(zombie => {
      val currentPosition = zombie.currentPosition(time - zombie.time)
      new Graph(
        currentPosition +: newGraph.vertices,
        AntoineGraph.addVertex(
          currentPosition, newGraph.neighboursMap, quadTree, inflatedEdges,
          forceInclusion = true
        )
      ).a_*(currentPosition, target, (z1, z2) => !(z1 - z2)) match {
        case Some(path) if path.tail.nonEmpty =>
          new Zombie(zombie.id, time, currentPosition, (path.tail.head - path.head).arg, moving = true)
        case _ =>
          new Zombie(zombie.id, time, currentPosition, zombie.direction, moving = false)
      }
    })
  }

}