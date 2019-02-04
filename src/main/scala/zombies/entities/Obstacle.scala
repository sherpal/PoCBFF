package zombies.entities

import main.Main
import physics.Complex
import physics.quadtree.ShapeQT
import physics.shape.{Polygon, Shape}

import scala.util.Random

final class Obstacle(val shape: Polygon, val time: Long) extends Body {

  val id: Long = Entity.newId()

  val pos: Complex = 0
  val rotation: Double = 0

  Obstacle._quadTree = Obstacle._quadTree :+ shape

}

object Obstacle {

  private var _quadTree: ShapeQT = ShapeQT(Main.worldBoundingBox)

  def quadTree: ShapeQT = _quadTree

  def reset(): Unit =
    _quadTree = ShapeQT(Main.worldBoundingBox)

  private val defaultObstacleRadius: Double = 30
  private val defaultShape: Polygon = Shape.regularPolygon(4, defaultObstacleRadius)

  def createNewObstacle(time: Long, quadTree: ShapeQT): Obstacle = {
    def tryPosition(): Obstacle = {
      val w = Main.worldBoundingBox.width - defaultObstacleRadius
      val h = Main.worldBoundingBox.height - defaultObstacleRadius
      val pos = Complex(
        Random.nextDouble() * w - w / 2,
        Random.nextDouble() * h - h / 2
      )

      if (!quadTree.collides(defaultShape, pos, 0))
        new Obstacle(Polygon(defaultShape.vertices.map(_ + pos), convex = true), time)
      else
        tryPosition()
    }

    tryPosition()
  }

  def createSomeObstacles(obstacleNbr: Int, time: Long, quadTree: ShapeQT): List[Obstacle] = {
    val (obstacles, qt) = (1 to obstacleNbr).foldLeft((List[Obstacle](), _quadTree)) {
      case ((obs, qtAcc), _) =>
        val newObstacle = createNewObstacle(time, qtAcc)
        (newObstacle +: obs, qtAcc :+ newObstacle.shape)
    }

    _quadTree = qt
    obstacles
  }

}