package physics.quadtree

trait QuadTree[T] {

  val elements: List[T]

  val subTrees: List[QuadTree[T]]

  protected def divide: List[QuadTree[T]]

  protected def shouldDivide: Boolean

  protected def shouldContain(u: T): Boolean

  protected def couldContain(t: T): Boolean

  def contains(t: T): Boolean =
    if (subTrees.isEmpty) elements.contains(t) else subTrees.filter(_.couldContain(t)).exists(_.contains(t))

  def :+(t: T): QuadTree[T]

  def toProtoString: String = {
    s"""
      |Contains: ${elements.mkString(", ")}
      |Children:
      |${subTrees.map(_.toProtoString).map(_.split("\n").map("\t" + _).mkString("\n")).mkString("\n")}
    """.stripMargin
  }

}

