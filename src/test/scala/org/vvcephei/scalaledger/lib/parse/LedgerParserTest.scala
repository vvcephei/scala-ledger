package org.vvcephei.scalaledger.lib.parse

import org.joda.time.DateTime
import org.scalatest.FunSuite
import org.vvcephei.scalaledger.lib.model._
import org.vvcephei.scalaledger.lib.parse.InternalLedgerParser._

import scala.io.Source

class LedgerParserTest extends FunSuite {


  test("date") {
    val parse1 = parseAll(date, "2015/02/14")
    assert(parse1.get === new DateTime(2015, 2, 14, 0, 0))
  }

  test("marker") {
    parseAll(marker, "*") match {
      case Success(s, y) => assert(s === "*")
      case o => fail(o.toString)
    }
    parseAll(marker.?, "*") match {
      case Success(s, y) => assert(s === Some("*"))
      case o => fail(o.toString)
    }
  }

  test("date marker") {
    parseAll(date ~ ws ~ marker.?, "2015/02/14 *") match {
      case Success(d ~ _ ~ Some("*"), y) => assert(d === new DateTime(2015, 2, 14, 0, 0))
      case o => fail(o.toString)
    }
  }

  test("transaction start") {
    val parse1 = parseAll(transactionStart, "2015/02/14 * (ASDF) the description ; a comment")
    assert(parse1.get === TransactionStart(
      new DateTime(2015, 2, 14, 0, 0),
      Some("*"),
      Some("ASDF"),
      "the description",
      Some(Comment("a comment"))))

  }

  test("transaction start2") {
    val parse1 = parseAll(transactionStart, "2015/02/14 the description")
    assert(parse1.get === TransactionStart(
      new DateTime(2015, 2, 14, 0, 0),
      None,
      None,
      "the description",
      None))
  }


  test("account") {
    val parse1 = parseAll(account, "Expenses:The Food:More Stuff")
    assert(parse1.get === "Expenses:The Food:More Stuff")
  }

  test("posting") {
    parseAll(posting, "    Expenses:Food:Groceries             $ 65.00") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    * Expenses:Food:Groceries             $65.00") match {
      case Success(s, y) =>
        assert(s === Posting(Some("*"), "Expenses:Food:Groceries", Some(Quantity("$", 65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    ! Expenses:Food:Groceries             -$65.00") match {
      case Success(s, y) =>
        assert(s === Posting(Some("!"), "Expenses:Food:Groceries", Some(Quantity("$", -65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    Expenses:Food:Groceries             $ -65.00") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", -65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    Expenses:Food:Groceries             -65.00 USD") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groceries", Some(Quantity("USD", -65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    Expenses:Food:Groc eries             -65.00 USD") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groc eries", Some(Quantity("USD", -65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    Expenses:Food:Groc eries  -65.00 USD") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groc eries", Some(Quantity("USD", -65.00)), None, None))
      case f => fail(f.toString)
    }

    parseAll(posting, "    Expenses:Food:Groc eries    ") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Expenses:Food:Groc eries", None, None, None))
      case f => fail(f.toString)
    }
  }

  test("stock") {
    parseAll(posting, "     Assets:Broker                       250 STK @ $20.00") match {
      case Success(s, y) =>
        assert(s === Posting(None, "Assets:Broker", Some(Quantity("STK", 250)), Some(Price("$",20.0)), None))
      case f => fail(f.toString)
    }
  }

  test("transaction with posting") {
    assert(parseAll(transaction, "2015/02/14 the description\n" +
      "    Expenses:Food:Groceries              $65.00\n" +
      "    Assets:The Bank                     -$65.00\n"
    ).get === Transaction(
      List(),
      TransactionStart(new DateTime(2015, 2, 14, 0, 0), None, None, "the description", None),
      List(Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 65)), None, None)),
        Right(Posting(None, "Assets:The Bank", Some(Quantity("$", -65)), None, None)))
    ))

    assert(parseAll(transaction, "2015/02/14 the description\n" +
      "    Expenses:Food:Groceries              $65.00\n" +
      "    Assets:The Bank                     -$60.00\n" +
      "    Assets:Cash                         \n"
    ).get === Transaction(
      List(),
      TransactionStart(new DateTime(2015, 2, 14, 0, 0), None, None, "the description", None),
      List(Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 65)), None, None)),
        Right(Posting(None, "Assets:The Bank", Some(Quantity("$", -60)), None, None)),
        Right(Posting(None, "Assets:Cash", None, None, None)))
    ))

    assert(parseAll(transaction, """2011/01/14 Bank
                                   |  ; Regular monthly savings transfer
                                   |  Assets:Savings                     $ 300.00
                                   |  Assets:Checking""".stripMargin + "\n"
    ).get === Transaction(
      List(),
      TransactionStart(new DateTime(2011, 1, 14, 0, 0), None, None, "Bank", None),
      List(
        Left(Comment("Regular monthly savings transfer")),
        Right(Posting(None, "Assets:Savings", Some(Quantity("$", 300)), None, None)),
        Right(Posting(None, "Assets:Checking", None, None, None)))
    ))
  }

  test("multiple transactions") {
    assert(parseAll(transactions,
      "\n" +
        "\n" +
        "\n" +
        "2015/02/14 the description\n" +
        "    Expenses:Food:Groceries              $65.00\n" +
        "    Assets:The Bank                     -$60.00\n" +
        "    Assets:Cash                         \n" +
        "\n" +
        "2015/02/14 the description\n" +
        "    Expenses:Food:Groceries              $6500.00\n" +
        "    Assets:The Bank                     -$6000.00\n" +
        "    Assets:Cash                         \n" +
        "\n" +
        "\n" +
        "\n" +
        "2015/02/14 the description\n" +
        "    Expenses:Food:Groceries              $6,500.00\n" +
        "    Assets:The Bank                     -$6,000.00\n" +
        "    Assets:Cash                         \n"
    ).get === List(Transaction(
      List(),
      TransactionStart(new DateTime(2015, 2, 14, 0, 0), None, None, "the description", None),
      List(Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 65)), None, None)),
        Right(Posting(None, "Assets:The Bank", Some(Quantity("$", -60)), None, None)),
        Right(Posting(None, "Assets:Cash", None, None, None)))
    ), Transaction(
      List(),
      TransactionStart(new DateTime(2015, 2, 14, 0, 0), None, None, "the description", None),
      List(Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 6500)), None, None)),
        Right(Posting(None, "Assets:The Bank", Some(Quantity("$", -6000)), None, None)),
        Right(Posting(None, "Assets:Cash", None, None, None)))
    ), Transaction(
      List(),
      TransactionStart(new DateTime(2015, 2, 14, 0, 0), None, None, "the description", None),
      List(Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 6500)), None, None)),
        Right(Posting(None, "Assets:The Bank", Some(Quantity("$", -6000)), None, None)),
        Right(Posting(None, "Assets:Cash", None, None, None))
      ))))
  }

  test("budget") {
    val input: String = "~ Monthly\n" +
      "    Expenses:Food:Groceries              $65.00\n" +
      "    Assets:The Bank                     -$60.00\n" +
      "    Assets:Cash                         \n"

    assert(parseAll(budget, input).get ===
    Budget(
      List(),
      BudgetStart("Monthly"),
      List(
        Right(Posting(None,"Expenses:Food:Groceries",Some(Quantity("$",65.0)),None, None)),
        Right(Posting(None,"Assets:The Bank",Some(Quantity("$",-60.0)),None, None)),
        Right(Posting(None,"Assets:Cash",None,None, None)))))

    assert(parseAll(budgets, input).get ===
    List(Budget(
      List(),
      BudgetStart("Monthly"),
      List(
        Right(Posting(None,"Expenses:Food:Groceries",Some(Quantity("$",65.0)), None,None)),
        Right(Posting(None,"Assets:The Bank",Some(Quantity("$",-60.0)),None, None)),
        Right(Posting(None,"Assets:Cash",None,None, None))))))

    assert(parseAll(ledger, input).get ===
      Ledger(
    List(Budget(
      List(),
      BudgetStart("Monthly"),
      List(
        Right(Posting(None,"Expenses:Food:Groceries",Some(Quantity("$",65.0)), None,None)),
        Right(Posting(None,"Assets:The Bank",Some(Quantity("$",-60.0)),None, None)),
        Right(Posting(None,"Assets:Cash",None,None, None))))),
      List()))
  }


  test("ldemo.dat") {
    val resource = getClass.getResource("ldemo.dat")
    val source = Source.fromURL(resource).bufferedReader()
    val result: InternalLedgerParser.ParseResult[Ledger] = parseAll(ledger, source)

    result  match {
      case Success(s, y) => assert(s === ldemo.ledgerAST)
      case o => fail(o.toString)
    }
  }
}
