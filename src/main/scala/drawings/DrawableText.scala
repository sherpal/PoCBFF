package drawings
import main.Main
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex

final class DrawableText(var text: String, var position: Complex, var size: Int) extends Drawable {

  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.strokeStyle = "white"
    ctx.font = s"${size}px Monaco"
    val (x, y) = Main.changeCoordinate(position)
    ctx.strokeText(text, x, y, maxWidth = 500)
  }

}
