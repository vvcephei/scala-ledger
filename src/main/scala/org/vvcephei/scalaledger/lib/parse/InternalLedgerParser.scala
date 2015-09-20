package org.vvcephei.scalaledger.lib.parse

import java.text.NumberFormat

import org.vvcephei.scalaledger.lib.model._
import org.vvcephei.scalaledger.lib.util.Formatters

import scala.util.parsing.combinator.RegexParsers

import scala.language.postfixOps

object InternalLedgerParser extends RegexParsers {

  override def skipWhitespace = false

  protected[parse] def ws = " " | "\t"

  protected[parse] def nl = "\r".? ~ "\n"

  protected[parse] def date = """\d\d\d\d\/\d\d\/\d\d""".r ^^ Formatters.dateForm.parseDateTime

  protected[parse] def cleared = "*"

  protected[parse] def pending = "!"

  protected[parse] def marker = cleared | pending

  protected[parse] def code = """[a-zA-Z0-9]+""".r

  protected[parse] def description = """[^;\n]+""".r ^^ { _.trim }

  protected[parse] def comment = ";" ~ (ws *) ~ """[^\n]*""".r ^^ { case _ ~ _ ~ c => Comment(c.trim) }

  protected[parse] def account = """\S+(\s(\S+))*""".r

  // The regex will match more than just well formatted numbers, but
  // NumberFormat takes care of the validation. The regex is just to
  // hoover up all the consecutive number-like characters
  protected[parse] def amount = """[0-9.,]+""".r ^^ { potentialNumber =>
    NumberFormat.getInstance().parse(potentialNumber).doubleValue()
  }

  protected[parse] def currency = """[^\s0-9]+""".r

  protected[parse] def neg = ("-" ?) ^^ { _.isDefined }

  protected[parse] def quantity =
    ((neg ~ currency ~ (ws *) ~ neg ~ amount) |
      (neg ~ amount ~ (ws *) ~ currency)) ^^ {

      case (n1: Boolean) ~ (curr: String) ~ _ ~ (n2: Boolean) ~ (amt: Double) =>
        Quantity(curr, if (n1 || n2) -1 * amt else amt)
      case (n: Boolean) ~ (amt: Double) ~ _ ~ (curr: String) =>
        Quantity(curr, if (n) -1 * amt else amt)
    }

  protected[parse] def price = ("@" ~ ws.* ~ neg ~ currency ~ ws.* ~ neg ~ amount) ^^ {
    case _ ~ _ ~ n1 ~ curr ~ _ ~ n2 ~ amt =>
      Price(curr, if (n1 || n2) -1 * amt else amt)
  }

  protected[parse] def budgetStart = "~" ~ ws ~ """Monthly|Yearly""".r ^^ {
    case _ ~ _ ~ period => BudgetStart(period)
  }


  protected[parse] def transactionStart =
    date ~
      ((ws +) ~ marker).? ~
      ((ws *) ~ "(" ~ code ~ ")").? ~
      (ws *) ~ description ~
      ((ws *) ~ comment).? ~
      (ws *) ^^ {

      case date ~ maybeMarker ~ wrappedCode ~ _ ~ description ~ prefixedComment ~ _ =>
        TransactionStart(
          date,
          for (_ ~ m <- maybeMarker) yield m,
          for (_ ~ code ~ _ <- wrappedCode) yield code,
          description,
          for (_ ~ comment <- prefixedComment) yield comment)
    }

  protected[parse] def posting =
    (ws +) ~
      (marker ~ ws).? ~
      account ~
      (ws ~ (ws +) ~ quantity).? ~
      ((ws *) ~ price).? ~
      ((ws *) ~ comment).? ~
      (ws *) ^^ {

      case _ ~ maybeMarker ~ acct ~ maybeQuantity ~ maybePrice ~ prefixedComment ~ _ =>
        val quantity1: Option[Quantity] = maybeQuantity match {
          case Some(_ ~ (q: Quantity)) => Some(q)
          case _ => None
        }
        val price1: Option[Price] = maybePrice match {
          case Some(_ ~ (p: Price)) => Some(p)
          case _ => None
        }
        Posting(
          for (m ~ _ <- maybeMarker) yield m,
          acct,
          quantity1,
          price1,
          for (_ ~ comment <- prefixedComment) yield comment
        )
    }

  protected[parse] def postingsListPartial =
    (posting ~ nl ~ ((ws +) ~ comment ~ nl).*) ~
      (posting ~ nl ~ ((ws +) ~ comment ~ nl).*) ~
      (posting ~ nl ~ ((ws +) ~ comment ~ nl).*).* ^^ {
      case p1 ~ p2 ~ ps => p1 :: p2 :: ps
    }

  protected[parse] def budget =
    (comment ~ nl).* ~ budgetStart ~ nl ~ ((ws +) ~ comment ~ nl).* ~ postingsListPartial ^^ {
      case comments ~ bstart ~ _ ~ morecomments ~ ps =>
        def readComments(cs: List[
          InternalLedgerParser.~[
            InternalLedgerParser.~[List[String], Comment],
            InternalLedgerParser.~[Option[String], String]]
          ]) =
          for (_ ~ c ~ _ <- cs) yield Left(c)

        val postings =
          readComments(morecomments) ++
            (for (p ~ _ ~ interComments <- ps) yield Right(p) :: readComments(interComments)).flatten

        Budget(
          for (comm ~ _ <- comments) yield comm,
          bstart,
          postings
        )
    }

  protected[parse] def transaction =
    (comment ~ nl).* ~
      transactionStart ~ nl ~
      ((ws +) ~ comment ~ nl).* ~
      postingsListPartial ^^ {

      case comments ~ tstart ~ _ ~ morecomments ~ ps =>
        def readComments(cs: List[
          InternalLedgerParser.~[
            InternalLedgerParser.~[List[String], Comment],
            InternalLedgerParser.~[Option[String], String]]
          ]) =
          for (_ ~ c ~ _ <- cs) yield Left(c)

        val postings =
          readComments(morecomments) ++
            (for (p ~ _ ~ interComments <- ps) yield Right(p) :: readComments(interComments)).flatten

        Transaction(
          for (comm ~ _ <- comments) yield comm,
          tstart,
          postings
        )
    }

  protected[parse] def budgets = (((whiteSpace ?) ~ budget) *) ^^ { matches =>
    for (_ ~ budget <- matches) yield budget
  }

  protected[parse] def transactions = (((whiteSpace ?) ~ transaction) *) ^^ { matches =>
    for (_ ~ transaction <- matches) yield transaction
  }

  protected[parse] def ledger = budgets ~ transactions ~ whiteSpace.* ^^ {
    case bs ~ ts ~ _ => Ledger(bs, ts)
  }
}
