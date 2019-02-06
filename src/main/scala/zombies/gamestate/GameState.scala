package zombies.gamestate

import main.Main
import physics.pathfinding.immutablegraph.{AntoineGraph, Graph}
import physics.quadtree.ShapeQT
import physics.shape.{BoundingBox, Segment}
import zombies.entities.{Obstacle, Player, PopStationContainer, Zombie}

final class GameState(
                       val startTime: Long,
                       val time: Long,
                       val zombies: List[Zombie],
                       val player: Player,
                       val obstacles: List[Obstacle],
                       val popStationContainer: PopStationContainer,
                       val graph: Graph,
                       val inflatedEdges: List[Segment],
                       val computationTimes: Map[String, Long]
                     ) {

  private def javaTime: Long = new java.util.Date().getTime

  def nextGameState(
                     newTime: Long,
                     quadTree: ShapeQT,
                     directions: List[Player.Direction]
                   ): Option[GameState] = {

    val popStationStartTime = javaTime
    val (newPopStationContainer, poppedZombies) = popStationContainer.updateZombiePopStations(
      newTime, ((time - startTime) / 30000 + 1).toInt, quadTree
    )
    val popStationComputationTime = javaTime - popStationStartTime

    val playerUpdateStartTime = javaTime
    val newPlayer = Player.updatePlayer(newTime, player, directions, quadTree, GameState.worldBox)
    val playerComputationTime = javaTime - playerUpdateStartTime

    val zombieUpdateStartTime = javaTime
    val newZombies = Zombie.updateZombies(newTime, zombies, newPlayer.pos, graph, quadTree, inflatedEdges)
    val zombieUpdateComputationTime = javaTime - zombieUpdateStartTime

    val allNewZombies = newZombies ++ poppedZombies

    if (allNewZombies.exists(_.collides(newPlayer))) {
      println(s"Game Over, you survived ${(newTime - startTime) / 1000} seconds!")
      None
    } else {
      Some(new GameState(
        startTime, newTime,
        allNewZombies, newPlayer, obstacles, newPopStationContainer,
        graph, inflatedEdges,
        Map[String, Long](
          "popStation" -> (popStationComputationTime + computationTimes.getOrElse("popStation", 0: Long)),
          "player" -> (playerComputationTime + computationTimes.getOrElse("player", 0: Long)),
          "zombiesUpdate" -> (zombieUpdateComputationTime + computationTimes.getOrElse("zombiesUpdate", 0: Long))
        )
      ))
    }

  }

}

object GameState {

  val worldBox: BoundingBox = Main.worldBoundingBox

  def startingGameState(nbrObstacles: Int = 10): GameState = {

    val startTime: Long = new java.util.Date().getTime

    Obstacle.reset()
    val obstacles = Obstacle.createSomeObstacles(nbrObstacles, startTime, Obstacle.quadTree)

    val quadTree = Obstacle.quadTree

    val (graph, _, inflatedEdges) = AntoineGraph(quadTree, Zombie.zombieRadius)

    val player = Player.startingPlayer(startTime, quadTree)

    new GameState(
      startTime, startTime,
      Nil, player, obstacles, new PopStationContainer(startTime, Nil, 0),
      graph, inflatedEdges,
      Map()
    )

  }

}