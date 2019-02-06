package components

import drawings.DrawableText
import main.Main
import org.scalajs.dom
import org.scalajs.dom.html
import zombies.entities.Obstacle
import zombies.gamestate.GameState
import zombies.entities.Player._
import zombies.gamedrawer.GameDrawer

import scala.collection.mutable

object BFFZ extends Component {

  val name: String = "BFFZ"

  private var gameState: Option[GameState] = None
  private val computationTimes: mutable.Map[String, Long] = mutable.Map()

  private var text: Option[DrawableText] = None

  private val pressedKeys: mutable.Set[String] = mutable.Set()

  private def onKeyPressed(event: dom.KeyboardEvent): Unit = {
    pressedKeys += event.key
  }

  private def onKeyReleased(event: dom.KeyboardEvent): Unit = {
    pressedKeys -= event.key
  }

  private def onClick(event: dom.MouseEvent): Unit = {
    if (gameState.isEmpty) {
      gameState = Some(GameState.startingGameState())
      computationTimes += "drawing" -> 0
      text.get.destroy()
    }
  }

  def onEnter(): Unit = {
    Main.canvas.onkeydown = onKeyPressed
    Main.canvas.onkeyup = onKeyReleased
    Main.canvas.onclick = onClick
    text = Some(new DrawableText("Click to start. ZQSD or arrows to move", -300, 30))
  }


  def onLeave(): Unit = {
    Main.canvas.onkeydown = null
    Main.canvas.onkeyup = null
    Main.canvas.onclick = null
    gameState = None
    text match {
      case Some(t) =>
        t.destroy()
      case None =>
    }
    text = None
  }

  val div: html.Div = dom.document.createElement("div").asInstanceOf[html.Div]


  def update(deltaTime: Double): Unit = {
    if (gameState.isDefined) {
      val currentTime: Long = new java.util.Date().getTime
      val startTime = gameState.get.startTime

      gameState = gameState.get.nextGameState(
        currentTime,
        Obstacle.quadTree,
        List(Up, Down, Right, Left).filter(_.keys.exists(pressedKeys.contains))
      )

      gameState match {
        case Some(gs) =>
          val startTime = new java.util.Date().getTime
          GameDrawer.drawGameState(gs, Main.ctx)
          computationTimes += "drawing" -> (computationTimes("drawing") + new java.util.Date().getTime - startTime)
          for {
            (key, value) <- gs.computationTimes
          } {
            computationTimes += key -> value
          }

        case None =>
          text = Some(new DrawableText(
            s"Game over.\nYou survived ${(currentTime - startTime) / 1000} seconds.\nClick to start again.",
            -250, 30
          ))
          println(computationTimes)
      }

    }
  }


  Component.registerComponent(this)

}
