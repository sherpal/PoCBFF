package main

import components._
import drawings.Drawable
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.CanvasRenderingContext2D
import physics.Complex
import physics.shape.BoundingBox
import utils.FPSRecorder

object Main {

  val canvas: html.Canvas = dom.document.getElementsByTagName("canvas")(0).asInstanceOf[html.Canvas]
  private val boundingRect = canvas.getBoundingClientRect()

  private val fpsDiv: html.Div = dom.document.getElementById("fps").asInstanceOf[html.Div]

  val worldBoundingBox: BoundingBox = BoundingBox(
    -canvas.width / 2, -canvas.height / 2, canvas.width / 2, canvas.height / 2
  )

  val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  var mousePosition: (Double, Double) = (0, 0)

  def mouseComplexPosition: Complex = changeCoordinate(mousePosition._1, mousePosition._2)

  canvas.onmousemove = (mouseEvent: dom.MouseEvent) => {
    mousePosition = (
      mouseEvent.clientX - boundingRect.left,
      mouseEvent.clientY - boundingRect.top
    )
  }

  def clear(): Unit = {
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  def changeCoordinate(worldPos: Complex): (Double, Double) = (
    worldPos.re + canvas.width / 2, canvas.height / 2 - worldPos.im
  )

  def changeCoordinate(x: Double, y: Double): Complex = Complex(x - canvas.width / 2, canvas.height / 2 - y)

  def main(args: Array[String]): Unit = {

    if (scala.scalajs.LinkingInfo.developmentMode) {
      ObstacleMaker
      FollowTheMouse
      TestAStar
      MovingMob
      BFFZ
    } else {
      BFFZ.render()
      Component.hideMenu()
    }

    clear()

    var lastTimeStamp = 0.0

    def run(ts: Double): Unit = {
      clear()

      val delta = ts - lastTimeStamp
      lastTimeStamp = ts

      Drawable.draw(ctx)

      dom.window.requestAnimationFrame(ts => run(ts))

      FollowTheMouse.update()
      MovingMob.update(delta)
      BFFZ.update(delta)
      FPSRecorder.addRecord(delta)
      fpsDiv.textContent = s"FPS: ${FPSRecorder.fps}"
    }

    dom.window.requestAnimationFrame(ts => run(ts))
  }

}
