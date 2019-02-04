package drawings

import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.collection.mutable

trait Drawable {

  def draw(ctx: CanvasRenderingContext2D): Unit

  Drawable.allDrawables += this

  def destroy(): Unit =
    Drawable.allDrawables -= this

  def reset(): Unit =
    Drawable.allDrawables += this

  var layer: Int = 0

}

object Drawable {

  private val allDrawables: mutable.Set[Drawable] = mutable.Set()

  def draw(ctx: CanvasRenderingContext2D): Unit =
    allDrawables.toList.sortBy(_.layer).foreach(_.draw(ctx))

}
