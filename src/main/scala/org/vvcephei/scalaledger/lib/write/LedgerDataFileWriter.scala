package org.vvcephei.scalaledger.lib.write

import java.io.{FileWriter, File}
import org.vvcephei.scalaledger.lib.model._
import org.vvcephei.scalaledger.lib.util.Formatters

case class LedgerDataFileWriter(f: File, append: Boolean = true) {
  private val writer = new FileWriter(f, append)

  private[write] def format(ledger: Ledger): String = {
    val budget = ledger.budget.map(format).mkString("\n\n")
    val transactions = ledger.transactions.map(format).mkString("\n\n")
    budget + "\n" + transactions + "\n"
  }

  private[write] def format(budget: Budget): String = {
    val comments: String = budget.comment.map("; " + _.comment).mkString("\n")
    val header: String = format(budget.budgetStart)
    val chars: List[String] = budget.postings.map(_.fold("; " + _, format))
    comments + "\n" + header + "\n" + chars.mkString("\n") + "\n"
  }

  private[this] def format(budgetStart: BudgetStart): String = {
    s"~ ${budgetStart.period}"
  }

  private[write] def format(transaction: Transaction): String = {
    val comments: String = transaction.comment.map("; " + _.comment).mkString("\n")
    val header: String = format(transaction.transactionStart)
    val chars: List[String] = transaction.postings.map(_.fold("; " + _, format))
    comments + "\n" + header + "\n" + chars.mkString("\n") + "\n"
  }

  private[this] def format(posting: Posting): String =
    "    " +
      (f"${posting.marker.map(_ + " ").getOrElse("") }${posting.account }%-40s  " +
        s"${posting.quantity.map(format).getOrElse("") } " +
        s"${posting.comment.map("; " + _).getOrElse("") }").trim

  private[this] def format(quantity: Quantity): String =
    s"${if (quantity.amount < 0) "-" else "" }${quantity.currency}${Formatters.decForm.format(quantity.amount.abs) }"

  private[this] def format(transactionStart: TransactionStart): String =
    Formatters.dateForm.print(transactionStart.date) +
      transactionStart.marker.map(" " + _).getOrElse("") +
      transactionStart.code.map(" (" + _ + ")").getOrElse("") +
      s" ${transactionStart.description}" +
      transactionStart.comment.map(" ; " + _).getOrElse("")

  def write(ledger: Ledger) = {
    writer.write(format(ledger))
    writer.flush()
  }

  def write(transaction: Transaction) = {
    writer.write("\n")
    writer.write(format(transaction))
  }

  def close() = writer.close()
}
