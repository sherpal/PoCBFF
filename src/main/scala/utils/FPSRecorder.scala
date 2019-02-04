package utils

import scala.collection.mutable

object FPSRecorder {

  private val nbrRecords: Int = 100

  private val records: mutable.Queue[Double] = mutable.Queue()

  (0 until nbrRecords).foreach(_ => records += 60)

  def fps: Long = math.round(records.min)

  def addRecord(delta: Double): Unit = {
    records.dequeue()
    records.enqueue(1000 / delta)
  }

}
