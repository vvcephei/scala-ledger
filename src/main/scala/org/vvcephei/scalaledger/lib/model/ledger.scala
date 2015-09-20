package org.vvcephei.scalaledger.lib.model

import org.joda.time.DateTime

sealed trait LedgerAST

case class BudgetStart(period: String)

case class Budget(comment: List[Comment],
                  budgetStart: BudgetStart,
                  postings: List[Either[Comment, Posting]])

case class TransactionStart(date: DateTime,
                            marker: Option[String],
                            code: Option[String],
                            description: String,
                            comment: Option[Comment]) extends LedgerAST

case class Comment(comment: String) extends LedgerAST

case class Quantity(currency: String, amount: Double) extends LedgerAST

case class Price(currency: String, amount: Double) extends LedgerAST

case class Posting(marker: Option[String], account: String, quantity: Option[Quantity], price: Option[Price], comment: Option[Comment])
  extends LedgerAST

case class Transaction(comment: List[Comment],
                       transactionStart: TransactionStart,
                       postings: List[Either[Comment, Posting]]) extends LedgerAST

case class Ledger(budget: List[Budget], transactions: List[Transaction]) extends LedgerAST
