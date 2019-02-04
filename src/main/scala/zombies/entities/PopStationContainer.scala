package zombies.entities

import main.Main
import physics.Complex
import physics.quadtree.ShapeQT
import zombies.entities.ZombiePopStation.shape

import scala.util.Random

final class PopStationContainer(
                                 val time: Long,
                                 val popStations: List[ZombiePopStation],
                                 val lastPop: Long
                               ) {

  def updateZombiePopStations(
                               currentTime: Long,
                               numberToPop: Int,
                               quadTree: ShapeQT
                             ): (PopStationContainer, List[Zombie]) = {

    def findPos(): Complex = {
      val tryPos = Complex(
        Random.nextDouble() * Main.worldBoundingBox.width - Main.worldBoundingBox.width / 2,
        Random.nextDouble() * Main.worldBoundingBox.height - Main.worldBoundingBox.height / 2
      )

      if (quadTree.collides(shape, tryPos, 0)) findPos()
      else tryPos
    }

    val newPopStations = if (time - lastPop > PopStationContainer.popTime)
      (0 until numberToPop).toList.map(_ => new ZombiePopStation(
        Entity.newId(), time, findPos()
      ))
    else List()

    val (popped, still) = popStations.partition(currentTime - _.time > PopStationContainer.gestation)

    val zombies = popped.map(station => new Zombie(
      Entity.newId(), currentTime, station.pos, 0, moving = false
    ))

    (
      new PopStationContainer(
        currentTime,
        still ++ newPopStations,
        if (newPopStations.nonEmpty) currentTime else lastPop
      ),
      zombies
    )
  }


}

object PopStationContainer {

  val gestation: Long = 2000

  val popTime: Long = 10000

}