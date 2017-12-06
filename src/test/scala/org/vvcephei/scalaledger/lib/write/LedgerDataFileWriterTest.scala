package org.vvcephei.scalaledger.lib.write

import java.io.File

import org.joda.time.DateTime
import org.scalatest.FunSuite
import org.vvcephei.scalaledger.lib.model._

class LedgerDataFileWriterTest extends FunSuite {
  val writer = LedgerDataFileWriter(new File("/dev/null"), append = false)

  test("transaction") {
    val result = writer.format(
      Transaction(Nil,
        TransactionStart(new DateTime(0L), None, None, "description", None),
        List(
          Right(Posting(None, "account", Some(Quantity("$", 15.0)), None, None))
        ))
    )
    val expected = "\n1969/12/31 description\n    account                                   $15.00\n"
    assert(result === expected)
  }

  test("ledger") {
    val ledger = Ledger(
      List(Budget(
        List(Comment("comm")),
        BudgetStart("Monthly"),
        List(
          Right(Posting(None, "account", Some(Quantity("$", 15.0)), None, None))))),
      List(Transaction(
        Nil,
        TransactionStart(new DateTime(0L), None, None, "description", None),
        List(
          Right(Posting(None, "account", Some(Quantity("$", 15.0)), None, None))
        )))
    )
    val expected = "; comm\n~ Monthly\n    account                                   $15.00\n\n\n1969/12/31 " +
      "description\n    account                                   $15.00\n\n"
    assert(writer.format(ledger) === expected)
  }
}
