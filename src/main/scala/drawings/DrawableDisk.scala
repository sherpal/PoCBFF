package drawings

import main.Main
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex
import physics.shape.Circle

final class DrawableDisk(center: Complex, radius: Double) extends Drawable {

  val circle: Circle = new Circle(radius)

  val canvasPosition: (Double, Double) = Main.changeCoordinate(center)

  val color: String = "white"

  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = color
    ctx.beginPath()
    ctx.arc(canvasPosition._1, canvasPosition._2, radius, 0, 2 * math.Pi)
    ctx.closePath()
    ctx.fill()
  }

}
