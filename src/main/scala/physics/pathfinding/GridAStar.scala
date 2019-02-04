package physics.pathfinding

import physics.Complex

object GridAStar {

  def apply(startPosition: Complex, endPosition: Complex,
            heuristicFunction: (Complex, Complex) => Double,
            isLegal: Complex => Boolean,
            spacing: Double): List[Complex] =
  if (!isLegal(endPosition)) List(startPosition, startPosition)
  else {

    def reconstructPath(cameFromMap: Map[Complex, Complex]): List[Complex] = {
      def withAccumulator(currentPath: List[Complex]): List[Complex] =
        if (currentPath.head == startPosition) currentPath
        else withAccumulator(cameFromMap(currentPath.head) +: currentPath)

      withAccumulator(List(endPosition))
    }

    def h(z: Complex): Double = heuristicFunction(z, endPosition)
    def d(z1: Complex, z2: Complex): Double = !(z1 - z2)

    val dir = (endPosition - startPosition).normalized * spacing
    val orthogonalDir = dir.orthogonal

//    val dir = Complex(spacing, 0)
//    val orthogonalDir = dir.orthogonal

    def neighbours(z: Complex): List[Complex] = List(
      z + dir,
      z + orthogonalDir,
      z - dir,
      z - orthogonalDir,
      z + dir + orthogonalDir,
      z + dir - orthogonalDir,
      z - dir + orthogonalDir,
      z - dir - orthogonalDir
    )

//    def neighbours(z: Complex): List[Complex] = List(
//      z + Complex(spacing, 0),
//      z + Complex(0, spacing),
//      z - Complex(spacing, 0),
//      z - Complex(0, spacing)
//    )
//
    final case class Score(f: Double, g: Double)
    val defaultScore = Score(Double.MaxValue, Double.MaxValue)

    def exploration(openSet: Map[Complex, Score], closedSet: Set[Complex], cameFromMap: Map[Complex, Complex]):
    Map[Complex, Complex] =
    if (openSet.isEmpty) Map()
    else {
      val (currentVertex, currentVertexScore) = openSet.minBy(_._2.f)

      if (d(currentVertex, endPosition) <= spacing) cameFromMap + (endPosition -> currentVertex)
      else {
        val newOpenSet = openSet - currentVertex
        val newClosedSet = closedSet + currentVertex

        val newElementsInOpenSet = neighbours(currentVertex).filterNot(closedSet.contains).filter(isLegal)
          .map(neighbour => (
            neighbour,
            currentVertexScore.g + d(currentVertex, neighbour),
            openSet.getOrElse(neighbour, defaultScore))
          )
          .filter(triplet => triplet._2 < triplet._3.g)
          .map({
            case (neighbour, tentativeScoreG, _) =>
              neighbour -> Score(tentativeScoreG + h(neighbour), tentativeScoreG)
          })
            .toMap

        exploration(
          newOpenSet ++ newElementsInOpenSet,
          newClosedSet,
          cameFromMap ++ newElementsInOpenSet.keys.map(_ -> currentVertex).toMap
        )
      }
    }

    val explorationResult = exploration(
      Map(startPosition -> Score(h(startPosition), 0)),
      Set(),
      Map()
    )

    if (explorationResult.isEmpty) List(startPosition, startPosition)
    else reconstructPath(explorationResult)
  }

}
