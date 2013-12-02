package org.vvcephei.scalaledger.lib.parse

import org.vvcephei.scalaledger.lib.model.{Posting, LedgerTransaction}
import org.joda.time.DateTime

object demo {
  val ledger =
    Ledger(List(
      LedgerTransaction(
        new DateTime(2010, 12, 1, 0, 0),
        Some("*"),
        None,
        "Checking balance",
        Nil,
        List(
          Posting("Assets:Checking", Some(1000.0), Nil),
          Posting("Equity:Opening Balances", Some(-1000.0), Nil))),
      LedgerTransaction(
        new DateTime(2010, 12, 20, 0, 0),
        Some("*"),
        None,
        "Organic Co-op",
        Nil,
        List(
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/01/01]")),
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/02/01]")),
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/03/01]")),
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/04/01]")),
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/05/01]")),
          Posting("Expenses:Food:Groceries", Some(37.5), List("[=2011/06/01]")),
          Posting("Assets:Checking", Some(-225.0), Nil))),
      LedgerTransaction(
        new DateTime(2010, 12, 28, 0, 0),
        None,
        None,
        "Acme Mortgage",
        Nil,
        List(
          Posting("Liabilities:Mortgage:Principal", Some(200.0), Nil),
          Posting("Expenses:Interest:Mortgage", Some(500.0), Nil),
          Posting("Expenses:Escrow", Some(300.0), Nil),
          Posting("* Assets:Checking", Some(-1000.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 2, 0, 0),
        None,
        None,
        "Grocery Store",
        Nil,
        List(
          Posting("Expenses:Food:Groceries", Some(65.0), Nil),
          Posting("* Assets:Checking", Some(-65.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 5, 0, 0),
        None,
        None,
        "Employer",
        Nil,
        List(
          Posting("* Assets:Checking", Some(2000.0), Nil),
          Posting("Income:Salary", Some(-2000.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 14, 0, 0),
        None,
        None,
        "Bank",
        List("Regular monthly savings transfer"),
        List(
          Posting("Assets:Savings", Some(300.0), Nil),
          Posting("Assets:Checking", Some(-300.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 19, 0, 0),
        None,
        None,
        "Grocery Store",
        Nil,
        List(
          Posting("Expenses:Food:Groceries", Some(44.0), List("hastag: not block")),
          Posting("Assets:Checking", Some(-44.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 25, 0, 0),
        None,
        None,
        "Bank",
        List("Transfer to cover car purchase"),
        List(
          Posting("Assets:Checking", Some(5500.0), Nil),
          Posting("Assets:Savings", Some(-5500.0), List(":nobudget:")))),
      LedgerTransaction(
        new DateTime(2011, 1, 25, 0, 0),
        None,
        None,
        "Tom's Used Cars",
        Nil,
        List(
          Posting("Expenses:Auto", Some(5500.0), List(":nobudget:")),
          Posting("Assets:Checking", Some(-5500.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 1, 27, 0, 0),
        None,
        None,
        "Book Store",
        Nil,
        List(
          Posting("Expenses:Books", Some(20.0), Nil),
          Posting("Liabilities:MasterCard", Some(-20.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 4, 25, 0, 0),
        None,
        None,
        "Tom's Used Cars",
        Nil,
        List(
          Posting("Expenses:Auto", Some(5500.0), List(":nobudget:")),
          Posting("Assets:Checking", Some(-5500.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 4, 27, 0, 0),
        None,
        None,
        "Bookstore",
        Nil,
        List(
          Posting("Expenses:Books", Some(20.0), Nil),
          Posting("Assets:Checking", Some(-20.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 12, 1, 0, 0),
        None,
        None,
        "Sale",
        Nil,
        List(
          Posting("Assets:Checking", Some(30.0), Nil),
          Posting("Income:Sales", Some(-30.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 12, 1, 0, 0),
        None,
        None,
        "Sale",
        Nil,
        List(
          Posting("Assets:Checking", Some(-30.0), Nil),
          Posting("Income:Sales", Some(30.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 12, 1, 0, 0),
        None,
        None,
        "Sale",
        Nil,
        List(
          Posting("Assets:Checking", Some(3000.0), Nil),
          Posting("Income:Sales", Some(-3000.0), Nil))),
      LedgerTransaction(
        new DateTime(2011, 12, 1, 0, 0),
        None,
        None,
        "Sale",
        Nil,
        List(
          Posting("Assets:Checking", Some(3000.0), Nil),
          Posting("Income:Sales and Stuff", Some(-3000.0), Nil)))))
}
