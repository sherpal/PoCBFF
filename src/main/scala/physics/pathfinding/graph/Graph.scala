package physics.pathfinding.graph

import physics.Complex

trait Graph {

  val vertices: Vector[Vertex]

  def addEdge(v1: Vertex, v2: Vertex): Unit = {
    v1.addNeighbour(v2)
    v2.addNeighbour(v1)
  }

  def isolateVertex(vertex: Vertex): Unit = {
    vertex.neighbours.foreach(_.removeNeighbour(vertex))
    vertex.isolate()
  }

  def aStar(startPos: Complex, endPos: Complex,
            heuristicFunction: (Vertex, Vertex) => Double, isLegal: Vertex => Boolean,
            minSpacing: Double): Option[Path] =
    if ((endPos - startPos).modulus2 < minSpacing * minSpacing) {
      Some(new Path(List(new Vertex(startPos), new Vertex(endPos))))
    } else {

    def distance(z_1: Complex, z_2: Complex): Double = (z_1 - z_2).modulus

    val startVertex = vertices.minBy(v => distance(v.position, startPos))
    val endVertex = vertices.minBy(v => distance(v.position, endPos))

    def h(vertex: Vertex): Double = heuristicFunction(vertex, endVertex)

    startVertex.g = 0
    startVertex.f = h(startVertex)

    def reconstructPath(currentPath: List[Vertex] = List(endVertex, new Vertex(endPos))): Path = {
      if (currentPath.head.cameFrom.isEmpty) new Path(new Vertex(startPos) +: currentPath)
      else reconstructPath(currentPath.head.cameFrom.get +: currentPath)
    }

    def findPath(openSet: Set[Vertex], closedSet: Set[Vertex]): Option[Path] = {
      if (openSet.isEmpty) {
        println("Could not find path...")
        None
      } else {
        val currentVertex = openSet.minBy(_.f)

        if (currentVertex.position == endVertex.position) {
          Some(reconstructPath())
        } else {
          val newOpenSet = openSet - currentVertex
          val newClosedSet = closedSet + currentVertex

          // removing vertices that are illegal
          currentVertex.neighbours.filterNot(isLegal).foreach(isolateVertex)

          val newOpenSetWithNeighbours = newOpenSet ++ (for {
            neighbour <- currentVertex.neighbours
            if !closedSet.contains(neighbour)
          } yield {
            val tentativeScore = currentVertex.g + distance(currentVertex.position, neighbour.position)
            val inOpenSet = openSet.contains(neighbour)
            if ((inOpenSet & tentativeScore < neighbour.g) | !inOpenSet) {
              neighbour.cameFrom = Some(currentVertex)
              neighbour.g = tentativeScore
              neighbour.f = neighbour.g + h(neighbour)
              Some(neighbour)
            } else None
          }).flatten

          findPath(newOpenSetWithNeighbours, newClosedSet)
        }
      }
    }

    findPath(Set(startVertex), Set())
  }

}

object Graph {

  def grid(left: Double, bottom: Double, right: Double, top: Double, spacing: Double): Graph = {
    val graphVertices = (for {
      x <- left to right by spacing
    } yield {
      (for (y <- bottom to top by spacing) yield new Vertex(Complex(x, y))).toVector
    }).toVector

    val graph = new Graph {
      val vertices: Vector[Vertex] = graphVertices.flatten
    }

    for (vs <- graphVertices) {
      vs.zip(vs.tail).foreach(edge => graph.addEdge(edge._1, edge._2))
    }

    for {
      (vs1, vs2) <- graphVertices.zip(graphVertices.tail)
      edge <- vs1.zip(vs2)
    } graph.addEdge(edge._1, edge._2)

    graph
  }

}