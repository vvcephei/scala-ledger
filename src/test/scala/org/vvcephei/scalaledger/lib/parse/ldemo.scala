package org.vvcephei.scalaledger.lib.parse

import org.joda.time.DateTime
import org.vvcephei.scalaledger.lib.model._

object ldemo {
  def dt(y: Int, m: Int, d: Int) = new DateTime(y, m, d, 0, 0)

  val ledgerAST =
    Ledger(
      List(
        Budget(
          List(),
          BudgetStart("Monthly"),
          List(
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 1000.0)), None, None)),
            Right(Posting(None, "Assets", None, None, None))
          )
        )
      ),
      List(
        Transaction(
          List(),
          TransactionStart(dt(2010, 12, 1), Some("*"), None, "Checking balance", None),
          List(
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", 1000.0)), None, None)),
            Right(Posting(None, "Equity:Opening Balances", None, None, None)))
        ),
        Transaction(
          List(),
          TransactionStart(dt(2010, 12, 20), Some("*"), None, "Organic Co-op", None),
          List(
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/01/01]")))),
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/02/01]")))),
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/03/01]")))),
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/04/01]")))),
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/05/01]")))),
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 37.5)), None, Some(Comment
              ("[=2011/06/01]")))),
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", -225.0)), None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2010, 12, 28), None, None, "Acme Mortgage", None),
          List(
            Right(Posting(None, "Liabilities:Mortgage:Principal", Some(Quantity("$", 200.0)), None, None)),
            Right(Posting(None, "Expenses:Interest:Mortgage", Some(Quantity("$", 500.0)), None, None)),
            Right(Posting(None, "Expenses:Escrow", Some(Quantity("$", 300.0)), None, None)),
            Right(Posting(Some("*"), "Assets:Checking", Some(Quantity("$", -1000.0)), None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 2), None, None, "Grocery Store", None),
          List(
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 65.0)), None, None)),
            Right(Posting(Some("*"), "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 5), None, None, "Employer", None),
          List(
            Right(Posting(Some("*"), "Assets:Checking", Some(Quantity("$", 2000.0)), None, None)),
            Right(Posting(None, "Income:Salary", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 14), None, None, "Bank", None),
          List(
            Left(Comment("Regular monthly savings transfer")),
            Right(Posting(None, "Assets:Savings", Some(Quantity("$", 300.0)), None, None)),
            Right(Posting(None, "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 19), None, None, "Grocery Store", None),
          List(
            Right(Posting(None, "Expenses:Food:Groceries", Some(Quantity("$", 44.0)), None, Some(Comment("hastag: not" +
              " " +
              "block")))),
            Right(Posting(None, "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 25), None, None, "Bank", None),
          List(
            Left(Comment("Transfer to cover car purchase")),
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", 5500.0)), None, None)),
            Right(Posting(None, "Assets:Savings", None, None, None)),
            Left(Comment(":nobudget:")))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 25), None, None, "Tom's Used Cars", None),
          List(
            Right(Posting(None, "Expenses:Auto", Some(Quantity("$", 5500.0)), None, None)),
            Left(Comment(":nobudget:")),
            Right(Posting(None, "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 1, 27), None, None, "Book Store", None),
          List(
            Right(Posting(None, "Expenses:Books", Some(Quantity("$", 20.0)), None, None)),
            Right(Posting(None, "Liabilities:MasterCard", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 4, 25), None, None, "Tom's Used Cars", None),
          List(
            Right(Posting(None, "Expenses:Auto", Some(Quantity("$", 5500.0)), None, None)),
            Left(Comment(":nobudget:")),
            Right(Posting(None, "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 4, 27), None, None, "Bookstore", None),
          List(
            Right(Posting(None, "Expenses:Books", Some(Quantity("$", 20.0)), None, None)),
            Right(Posting(None, "Assets:Checking", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 12, 1), None, None, "Sale", None),
          List(
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", 30.0)), None, None)),
            Right(Posting(None, "Income:Sales", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 12, 1), None, None, "Sale", None),
          List(
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", -30.0)), None, None)),
            Right(Posting(None, "Income:Sales", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 12, 1), None, None, "Sale", None),
          List(
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", 3000.0)), None, None)),
            Right(Posting(None, "Income:Sales", None, None, None)))),
        Transaction(
          List(),
          TransactionStart(dt(2011, 12, 1), None, None, "Sale", None),
          List(
            Right(Posting(None, "Assets:Checking", Some(Quantity("$", 3000.0)), None, None)),
            Right(Posting(None, "Income:Sales and Stuff", None, None, None))))
      )
    )
}
