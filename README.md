scala-ledger
============

A JVM library for reading and writing Ledger data files (http://www.ledger-cli.org/).

I wrote it to support another project of mine, but I'd be happy to maintain it if it's generally useful.

Build
-----

[![Build Status](https://travis-ci.org/vvcephei/scala-ledger.svg?branch=master)](https://travis-ci.org/vvcephei/scala-ledger)

compile: ```sbt compile```

test: ```sbt test```

Usage
-----

```scala
val ledger: Ledger = LedgerDataFileParser parse Source.fromFile(ledgerFile).getLines()
// the Ledger is a sequence of comments, period transactions, or regular transactions.

val ledgerWriter = LedgerDataFileWriter(ledgerFile, append = true)

try{
ledgerWriter.write(
    LedgerTransaction(
      date = new DateTime(),
      marker = None,
      code = None,
      description = "the descrptions",
      notes = Nil,
      postings = List(
        Posting("Expenses:A", Some(1.0)),
        Posting("Assets:B", Some(-1.0))
      ))
  )
} finally {
  ledgerWriter.close()
}

```
