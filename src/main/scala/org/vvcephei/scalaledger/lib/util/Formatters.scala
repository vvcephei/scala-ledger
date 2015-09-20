package org.vvcephei.scalaledger.lib.util

import java.text.DecimalFormat

import org.joda.time.format.DateTimeFormat

object Formatters {
  val dateForm = DateTimeFormat.forPattern("YYYY/MM/dd")
  // I want it to print at least 2 0s after the decimal, but include as many as possible.
  // I couldn't find a good way to print the max number of decimal places, so I played around, and 16 seemed to get
  // good results
  val decForm: DecimalFormat = new DecimalFormat("0.00##############")

}
