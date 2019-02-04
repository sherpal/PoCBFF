package zombies.gamedrawer

import main.Main
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex
import physics.shape.{Circle, Polygon}
import zombies.entities.{Obstacle, Player, Zombie, ZombiePopStation}
import zombies.gamestate.GameState

object GameDrawer {

  private val zombieColor: String = "#ccc"
  private val playerColor: String = "blue"
  private val obstacleColor: String = "white"
  private val popStationColor: String = "red"

  private def polygonPath(
                           polygon: Polygon,
                           translation: Complex, rotation: Double,
                           ctx: CanvasRenderingContext2D
                         ): Unit = {
    val canvasCoordinates = polygon.vertices.map(translation + _ * Complex.rotation(rotation))
      .map(Main.changeCoordinate)
    ctx.beginPath()
    ctx.moveTo(canvasCoordinates(0)._1, canvasCoordinates(0)._2)
    for ((x, y) <- canvasCoordinates.tail) {
      ctx.lineTo(x, y)
    }
    ctx.closePath()
    ctx.fill()
  }

  private def circlePath(
                          circle: Circle,
                          translation: Complex,
                          ctx: CanvasRenderingContext2D
                        ): Unit = {
    ctx.beginPath()
    val (x, y) = Main.changeCoordinate(translation)
    ctx.arc(x, y, circle.radius, 0, 2 * math.Pi)
    ctx.closePath()
    ctx.fill()
  }

  private def drawZombies(zombies: List[Zombie], ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = zombieColor
    for (zombie <- zombies) {
      polygonPath(zombie.shape, zombie.pos, zombie.rotation, ctx)
    }
  }

  private def drawPlayer(player: Player, ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = playerColor
    circlePath(player.shape, player.pos, ctx)
  }

  private def drawObstacles(obstacles: List[Obstacle], ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = obstacleColor
    for (obstacle <- obstacles) {
      polygonPath(obstacle.shape, 0, 0, ctx)
    }
  }

  private def drawPopStations(popStations: List[ZombiePopStation], ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = popStationColor
    for (station <- popStations) {
      circlePath(station.shape, station.pos, ctx)
    }
  }

  def drawGameState(gameState: GameState, ctx: CanvasRenderingContext2D): Unit = {
    drawObstacles(gameState.obstacles, ctx)
    drawPopStations(gameState.popStationContainer.popStations, ctx)
    drawZombies(gameState.zombies, ctx)
    drawPlayer(gameState.player, ctx)
  }

}
