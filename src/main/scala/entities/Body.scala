package entities

import physics.Complex
import physics.shape.Shape

trait Body {

  val shape: Shape

  var position: Complex
  val speed: Double
  var rotation: Double

  var moving: Boolean


}
