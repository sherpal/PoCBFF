package drawings

import main.Main
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex
import physics.shape.Polygon

final class DrawablePolygon(val shape: Polygon) extends DrawableShape {

  var color: String = "white"

  var translation: Complex = 0

  var rotation: Double = 0

  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = color
    ctx.beginPath()
    val vertices = shape.vertices.map(translation + Complex.rotation(rotation) * _).map(Main.changeCoordinate)

    ctx.moveTo(vertices(0)._1, vertices(0)._2)

    for {
      (x, y) <- vertices.tail
    } {
      ctx.lineTo(x, y)
    }
    ctx.closePath()
    ctx.fill()
  }

}
