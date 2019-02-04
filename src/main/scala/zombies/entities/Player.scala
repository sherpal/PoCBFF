package zombies.entities

import main.Main
import physics.Complex
import physics.quadtree.ShapeQT
import physics.shape.{BoundingBox, Circle}

import scala.util.Random

final class Player(
                    val id: Long,
                    val time: Long,
                    val pos: Complex,
                    val direction: Double,
                    val moving: Boolean
                  ) extends MovingBody {

  val shape: Circle = Player.shape

  val speed: Double = Player.playerSpeed

  val rotation: Double = 0

}

object Player {

  val playerRadius: Double = 10
  val playerSpeed: Double = 100

  val shape: Circle = new Circle(playerRadius)

  import Complex.i
  private val zero: Complex = 0

  sealed trait Direction {
    val z: Complex
    val keys: List[String]
  }
  case object Up extends Direction {
    val z: Complex = i
    val keys: List[String] = List("z", "ArrowUp")
  }
  case object Down extends Direction {
    val z: Complex = -i
    val keys: List[String] = List("s", "ArrowDown")
  }
  case object Right extends Direction {
    val z: Complex = 1
    val keys: List[String] = List("d", "ArrowRight")
  }
  case object Left extends Direction {
    val z: Complex = -1
    val keys: List[String] = List("q", "ArrowLeft")
  }

  private def findDirection(directions: List[Direction]): (Double, Boolean) = {
    val towards = directions.map(_.z).sum
    (towards.arg, towards != zero)
  }

  def updatePlayer(
                    time: Long,
                    player: Player,
                    directions: List[Direction],
                    quadTree: ShapeQT,
                    worldBox: BoundingBox
                  ): Player = {
    val (direction, moving) = findDirection(directions)

    if (moving) {
      val newPos = player.lastValidPos(time - player.time, quadTree, direction)

      val x = if (newPos.re > worldBox.right) worldBox.right
      else if (newPos.re < worldBox.left) worldBox.left
      else newPos.re
      val y = if (newPos.im > worldBox.top) worldBox.top
      else if (newPos.im < worldBox.bottom) worldBox.bottom
      else newPos.im

      new Player(player.id, time, Complex(x, y), direction, moving)
    } else new Player(player.id, time, player.pos, direction, moving)
  }

  def startingPlayer(time: Long, quadTree: ShapeQT): Player = {

    def findPosition(): Complex = {
      val w = Main.worldBoundingBox.width - playerRadius
      val h = Main.worldBoundingBox.height - playerRadius
      val tryNext = Complex(
        Random.nextDouble() * w - w / 2, Random.nextDouble() * h - h / 2
      )

      if (quadTree.collides(shape, tryNext, 0)) findPosition()
      else tryNext
    }

    new Player(Entity.newId(), time, findPosition(), 0, false)

  }

}
