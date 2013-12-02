package org.vvcephei.scalaledger.lib.parse

import scala.io.Source
import org.scalatest.FunSuite

class LedgerDataFileParserTest extends FunSuite {
  val demoLoaded = Source.fromURL(getClass.getResource("demo.ledger")).getLines()


  test("parseDemo") {
    val parse: Ledger = LedgerDataFileParser.parse(demoLoaded)
    assert(parse == demo.ledger)
  }
}
