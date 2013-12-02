package org.vvcephei.scalaledger.lib.model

import org.joda.time.DateTime

trait LedgerItem

case class Posting(account: String, amount: Option[Double], notes: List[String] = Nil)

case class PeriodTransaction(period: String, notes: List[String] = Nil, postings: List[Posting] = Nil) extends LedgerItem

case class LedgerTransaction(date: DateTime, marker: Option[String], code: Option[String], description: String, notes: List[String] = Nil, postings: List[Posting] = Nil) extends LedgerItem

case class Comment(comment: List[String]) extends LedgerItem
