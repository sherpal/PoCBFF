package components

import org.scalajs.dom
import org.scalajs.dom.html

import scala.collection.mutable


trait Component {

  val name: String

  val div: html.Div

  Component.components += this

  def render(): Unit = Component.changeComponent(this)

  /**
    * Callback when the component is left for another one
    */
  def onLeave(): Unit

  /**
    * Callback when the component is displayed.
    */
  def onEnter(): Unit

}

object Component {

  private val menu: html.Div = dom.document.getElementById("components-menu").asInstanceOf[html.Div]

  def hideMenu(): Unit = {
    menu.style.display = "none"
  }

  def showMenu(): Unit = {
    menu.style.display = "block"
  }

  def registerComponent(component: Component): Unit = {
    val button = dom.document.createElement("button").asInstanceOf[html.Button]
    button.textContent = component.name
    button.style.display = "block"
    menu.appendChild(button)
    button.onclick = (_: dom.MouseEvent) => {
      component.render()
    }
  }

  private val componentContainer: html.Div = dom.document.getElementById("app-container")
    .asInstanceOf[html.Div]

  private val components: mutable.Set[Component] = mutable.Set()

  private def changeComponent(component: Component): Unit = {
    while (componentContainer.hasChildNodes())
      componentContainer.removeChild(componentContainer.firstChild)

    components.filterNot(_ == component).foreach(_.onLeave())
    component.onEnter()

    componentContainer.appendChild(component.div)
  }

}
