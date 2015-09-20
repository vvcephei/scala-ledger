package org.vvcephei.scalaledger.lib.parse

import java.io.{StringReader, Reader, File}

import org.vvcephei.scalaledger.lib.model.{Transaction, Ledger}

import scala.io.Source


object LedgerParser {

  class LedgerParseException(message: String) extends RuntimeException

  def main(args: Array[String]): Unit = {
    val file = new File(args(0))
    val ledger: Ledger = parse(file)
    println(ledger)
  }

  def parse(file: File): Ledger = parse(Source.fromFile(file).reader())
  def parse(reader: Reader): Ledger = internalParse(reader)
  def parse(string: String): Ledger = internalParse(new StringReader(string))


  private def internalParse(reader: Reader): Ledger = {
    val result: InternalLedgerParser.ParseResult[Ledger] =
      InternalLedgerParser.parseAll(InternalLedgerParser.ledger, reader)

    val transactions =
      result match {
        case InternalLedgerParser.Success(res, rest) if rest.atEnd => res
        case InternalLedgerParser.NoSuccess(msg, rest) =>
          println(result)
          throw new LedgerParseException(result.toString)
      }
    transactions
  }
}
