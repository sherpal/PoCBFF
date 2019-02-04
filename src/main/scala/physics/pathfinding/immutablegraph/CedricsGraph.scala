package physics.pathfinding.immutablegraph

import physics.Complex
import physics.shape._

object CedricsGraph {

  private implicit class ShapeWithEdge(shape: Shape) {

    def edges: Vector[Segment] = shape match {
      case polygon: Polygon =>
        polygon.vertices.zip(polygon.vertices.tail :+ polygon.vertices(0))
          .map(Segment.tupled)
    }

    def shapeVertices: Vector[Complex] = shape match {
      case polygon: Polygon =>
        polygon.vertices
    }

    def shapeVerticesWithMiddle: Vector[Complex] = shape match {
      case polygon: Polygon =>
        polygon.vertices ++
          polygon.vertices.zip(polygon.vertices.tail :+ polygon.vertices(0)).map(zs => (zs._1 + zs._2) / 2)
    }

  }

  def apply(worldBoundingBox: BoundingBox, obstacles: List[Shape]): Graph = {

    val startTime = new java.util.Date().getTime

    val allSegments: Vector[Segment] = Vector(
      Segment(
        Complex(worldBoundingBox.left, worldBoundingBox.bottom),
        Complex(worldBoundingBox.left, worldBoundingBox.top)
      ),
      Segment(
        Complex(worldBoundingBox.right, worldBoundingBox.bottom),
        Complex(worldBoundingBox.right, worldBoundingBox.top)
      ),
      Segment(
        Complex(worldBoundingBox.left, worldBoundingBox.bottom),
        Complex(worldBoundingBox.right, worldBoundingBox.bottom)
      ),
      Segment(
        Complex(worldBoundingBox.left, worldBoundingBox.top),
        Complex(worldBoundingBox.right, worldBoundingBox.top)
      )
    ) ++ obstacles.flatMap(_.edges)

    val allVertices: Vector[Complex] =
      (worldBoundingBox.vertices ++ obstacles.flatMap(_.shapeVertices)).sortBy(_.im)



    val allLines = (allVertices ++ allVertices.zip(allVertices.tail).map(elem => (elem._1 + elem._2) / 2))
      .map(z => (z, Segment(Complex(worldBoundingBox.left - 10, z.im), Complex(worldBoundingBox.right + 10, z.im))))

    def intersectionWithLine(segment: Segment, line: Segment): Option[Complex] = {
      val lineHeight = line.z1.im
      if ((segment.z1.im < lineHeight && segment.z2.im > lineHeight)
        || (segment.z2.im < lineHeight && segment.z1.im > lineHeight)
      ) {
        segment.intersectionPoint(line)
      } else None
    }

//    val intersectionPoints = allLines.map({
//      case (vertex, line) => vertex +: allSegments.flatMap(intersectionWithLine(_, line))
//    })

    val vertices = allVertices.map(
      v =>
        (
          v,
          Segment(Complex(worldBoundingBox.left - 10, v.im), v - 1),
          Segment(v + 1, Complex(worldBoundingBox.right + 10, v.im))
        )
    ).flatMap {
      case (v, leftSegment, rightSegment) =>
        val pointsToTheLeft = allSegments.flatMap(intersectionWithLine(_, leftSegment))
        val pointsToTheRight = allSegments.flatMap(intersectionWithLine(_, rightSegment))
        val leftPoint = if (pointsToTheLeft.nonEmpty) Some(pointsToTheLeft.maxBy(_.re)) else None
        val rightPoint = if (pointsToTheRight.nonEmpty) Some(pointsToTheRight.minBy(_.re)) else None

        List(leftPoint, rightPoint).flatten.map(z => (z + v) / 2)
    }

//    val vertices = intersectionPoints.filter(points => points.nonEmpty && points.tail.nonEmpty)
//      .map(_.sortBy(_.re))
//      .flatMap(
//      points => points.zip(points.tail).map({
//        case (z1, z2) => (z1 + z2) / 2
//      })
//    )

    println(s"It took ${new java.util.Date().getTime - startTime} to compute.")

    new Graph(vertices, Map())
  }

}
