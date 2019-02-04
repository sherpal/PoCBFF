package physics.quadtree

import physics.shape.{BoundingBox, Polygon, Shape}

trait ShapeQuadTree extends QuadTree[Shape] {

  val threshold: Double

  protected val boundingBox: BoundingBox

  protected val rectangle: Polygon = Polygon(boundingBox.vertices)

  protected def shouldDivide: Boolean = boundingBox.size > threshold

  protected def couldContain(t: Shape): Boolean

  protected def shouldContain(shape: Shape): Boolean =
    shape.collides(0, 0, rectangle, 0, 0)

}
