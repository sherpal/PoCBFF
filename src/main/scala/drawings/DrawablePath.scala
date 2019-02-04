package drawings

import main.Main
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex

final class DrawablePath(val vertices: Vector[Complex], cycle: Boolean) extends Drawable {

  var color: String = "white"

  private val canvasCoordinates = vertices.map(Main.changeCoordinate)

  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.strokeStyle = color
    ctx.beginPath()
    ctx.moveTo(canvasCoordinates(0)._1, canvasCoordinates(0)._2)
    for ((x, y) <- canvasCoordinates.tail) ctx.lineTo(x, y)
    if (cycle)
      ctx.closePath()
    ctx.stroke()
  }

}
