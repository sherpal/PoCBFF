package physics.pathfinding.graph

import physics.Complex

import scala.collection.mutable

final class Vertex(val position: Complex) {

  private val _neighbours: mutable.Set[Vertex] = mutable.Set()

  def neighbours: Set[Vertex] = _neighbours.toSet

  def addNeighbour(vertex: Vertex): Unit = {
    _neighbours += vertex
  }

  def removeNeighbour(vertex: Vertex): Unit = {
    _neighbours -= vertex
  }

  def isolate(): Unit =
    _neighbours.clear()

  // These values are initiated in case of applying the A* algorithm
  var f: Double = Double.MaxValue
  var g: Double = Double.MaxValue
  var cameFrom: Option[Vertex] = None

}
