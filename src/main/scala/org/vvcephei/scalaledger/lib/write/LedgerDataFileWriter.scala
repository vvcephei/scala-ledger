package org.vvcephei.scalaledger.lib.write

import java.io.{FileWriter, File}
import org.vvcephei.scalaledger.lib.model._
import org.joda.time.format.DateTimeFormat
import org.vvcephei.scalaledger.lib.model.Comment
import org.vvcephei.scalaledger.lib.model.LedgerTransaction
import org.vvcephei.scalaledger.lib.model.PeriodTransaction

case class LedgerDataFileWriter(f: File, append: Boolean = true) {
  private val writer = new FileWriter(f, append)

  private val df = DateTimeFormat.forPattern("YYYY/MM/dd")

  private def formatComment(cs: List[String]) = cs map { "; " + _ } mkString "\n"

  private def moneyFormatter(amt: Double) = "$%.2f".format(amt).replace("$-", "-$")

  private def formatPosting(p: Posting, indent: String) = p.account + (p.amount map { a=> "\t" + moneyFormatter(a) } getOrElse "") +
    (p.notes match {
      case Nil => ""
      case n :: Nil => "\t; " + n
      case n :: l => "\t; " + n + "\n" + (l map { indent + "; " + _ } mkString "\n")
    })

  private def formatLT(lt: LedgerTransaction) = {
    val head = df.print(lt.date) + lt.marker.map("\t" + _).getOrElse("") + lt.code.map("\t(" + _ + ")").getOrElse("") + "\t" + lt.description
    val notes = lt.notes map { "\t; " + _ } mkString "\n"
    val postings = lt.postings map { p => "\t" + formatPosting(p, "\t") } mkString "\n"

    val notesBlock = if (notes == "") "" else "\n" + notes
    val postingsBlock = if (postings == "") "" else "\n" + postings

    head + notesBlock + postingsBlock
  }

  private def formatPT(pt: PeriodTransaction) = {
    val head = "~\t" + pt.period
    val notes = pt.notes map { "\t; " + _ } mkString "\n"
    val postings = pt.postings map { p => "\t" + formatPosting(p, "\t") } mkString "\n"

    val notesBlock = if (notes == "") "" else "\n" + notes
    val postingsBlock = if (postings == "") "" else "\n" + postings

    head + notesBlock + postingsBlock
  }

  def write(item: LedgerItem) = {
    writer.write("\n" + (item match {
      case Comment(cs) => formatComment(cs)
      case lt: LedgerTransaction => formatLT(lt)
      case pt: PeriodTransaction => formatPT(pt)
    }) + "\n")
    writer.flush()
  }

  def close() = writer.close()
}
