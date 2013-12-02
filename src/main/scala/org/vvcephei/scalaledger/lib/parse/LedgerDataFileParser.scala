package org.vvcephei.scalaledger.lib.parse

import org.joda.time.format.DateTimeFormat
import org.vvcephei.scalaledger.lib.model._
import org.vvcephei.scalaledger.lib.model.LedgerTransaction
import scala.Some
import org.vvcephei.scalaledger.lib.model.Comment
import org.vvcephei.scalaledger.lib.model.PeriodTransaction


case class Ledger(contents: List[LedgerItem]) {
  def transactions: List[LedgerTransaction] = contents filter {
    _.isInstanceOf[LedgerTransaction]
  } map {
    _.asInstanceOf[LedgerTransaction]
  }
}

/*
TODO: handle
     comment
         This is a block comment with
         multiple lines
     end comment
 */

object LedgerDataFileParser {
  private val PeriodTransactionStartR = """~\s*(.*)""".r("period")
  private val TransactionStartR = """^(\d\S*)\s+((\*|!)\s+)?((\([^)]*\))\s+)?(.*)$""".r("date", "_markgrp", "marker", "_codegrp", "code", "description")
  private val PostingR = """^\s+(\S+(\s*[^-\$\s]*)*)(\s+-?\$\s*-?[0-9,]+\.\d+)?(\s*;\s*(.*))?$""".r("account", "_", "amount", "_n", "note")
  // TODO. Have to rethink this to make it work with other currencies
  private val CommentR = """^[;#%|*]\s*(.*)""".r("comment")
  private val IndentedCommentR = """^\s+;\s*(.*)""".r("comment")

  def parse(lines: Iterator[String]): Ledger = {
    var periodTransaction: Option[PeriodTransaction] = None
    var transaction: Option[LedgerTransaction] = None
    var comment: Option[Comment] = None

    def finalizePT(trans: Option[PeriodTransaction]) = trans match {
      case None => None
      case Some(pt) =>
        val amounts = pt.postings filter { _.amount.isDefined } map { _.amount.get }
        if (amounts.size == pt.postings.size) trans
        else if (amounts.size == pt.postings.size - 1) {
          val balance = 0.0 - amounts.sum
          def fillMissing(l: List[Posting], r: List[Posting]): List[Posting] = l match {
            case Nil => r
            case Posting(a, None, n) :: rest => fillMissing(rest, Posting(a, Some(balance), n) :: r)
            case p :: rest => fillMissing(rest, p :: r)
          }
          Some(pt.copy(postings = fillMissing(pt.postings, Nil).reverse))
        } else throw new IllegalStateException("non-zero or -one postings with elided amounts")
    }

    def finalizeLT(trans: Option[LedgerTransaction]) = trans match {
      case None => None
      case Some(pt) =>
        val amounts = pt.postings filter { _.amount.isDefined } map { _.amount.get }
        if (amounts.size == pt.postings.size) trans
        else if (amounts.size == pt.postings.size - 1) {
          val balance = 0 - amounts.sum
          def fillMissing(l: List[Posting], r: List[Posting]): List[Posting] = l match {
            case Nil => r
            case Posting(a, None, n) :: rest => fillMissing(rest, Posting(a, Some(balance), n) :: r)
            case p :: rest => fillMissing(rest, p :: r)
          }
          Some(pt.copy(postings = fillMissing(pt.postings, Nil).reverse))
        } else throw new IllegalStateException("non-zero or -one postings with elided amounts")
    }

    def closePrevious() = {
      val tmp = List(finalizePT(periodTransaction), finalizeLT(transaction), comment).flatten
      periodTransaction = None
      transaction = None
      comment = None
      tmp
    }

    def appendToTransaction(posting: Posting) =
      (periodTransaction, transaction) match {
        case (Some(pt), None) =>
          periodTransaction = Some(pt.copy(postings = pt.postings ::: (posting :: Nil)))
        case (None, Some(t)) =>
          transaction = Some(t.copy(postings = t.postings ::: (posting :: Nil)))
        case (Some(a), Some(b)) => throw new IllegalStateException("Two open transactions: %s, %s".format(a, b))
        case (None, None) => throw new IllegalStateException("No open transactions")
      }

    def appendCommentToTransactionOrPosting(c: String) =
      (periodTransaction, transaction) match {
        case (Some(pt), None) =>
          pt.postings.reverse match {
            case Posting(acct, amt, notes) :: prev =>
              val newPost = Posting(acct, amt, notes ::: (c :: Nil))
              val newList = (newPost :: prev).reverse
              periodTransaction = Some(pt.copy(postings = newList))
            case Nil =>
              periodTransaction = Some(pt.copy(notes = pt.notes ::: (c :: Nil)))
          }
        case (None, Some(t)) =>
          t.postings.reverse match {
            case Posting(acct, amt, notes) :: prev =>
              val newPost = Posting(acct, amt, notes ::: (c :: Nil))
              val newList = (newPost :: prev).reverse
              transaction = Some(t.copy(postings = newList))
            case Nil =>
              transaction = Some(t.copy(notes = t.notes ::: (c :: Nil)))
          }
        case (Some(a), Some(b)) => throw new IllegalStateException("Two open transactions: %s, %s".format(a, b))
        case (None, None) => throw new IllegalStateException("No open transactions")
      }

    def appendComment(line: String) =
      comment match {
        case Some(c) => comment = Some(c.copy(comment = c.comment ::: (line :: Nil)))
        case None => comment = Some(Comment(line :: Nil))
      }

    def toOption[T](value: T) = if (value == null) None else Some(value)
    def toDouble(value: String) =
      if (value == null) None
      else Some(value.replace("$", "").replace(",", "").toDouble)

    val result =
      for (line <- lines) yield {
        line match {
          case PeriodTransactionStartR(period) =>
            val r = closePrevious()
            periodTransaction = Some(PeriodTransaction(period))
            r
          case TransactionStartR(date, _, marker, _, code, description) =>
            val r = closePrevious()
            transaction = Some(LedgerTransaction(toDate(date), toOption(marker), toOption(code), description))
            r
          case IndentedCommentR(c) =>
            appendCommentToTransactionOrPosting(c)
            Nil
          case PostingR(account, _, amount, _, null) =>
            appendToTransaction(Posting(account.trim, toDouble(amount)))
            Nil
          case PostingR(account, _, amount, _, note) =>
            appendToTransaction(Posting(account.trim, toDouble(amount), List(note)))
            Nil
          case CommentR(c) =>
            appendComment(c)
            Nil
          case _ =>
            closePrevious() // blank lines n stuff
        }
      }

    val items: List[List[LedgerItem with Product with Serializable]] = result.toList ::: (closePrevious() :: Nil)
    val flatten: List[LedgerItem with Product with Serializable] = items.flatten
    Ledger(flatten)
  }

  private val df = DateTimeFormat.forPattern("YYYY/MM/dd")

  private def toDate(str: String) = df parseDateTime str
}
