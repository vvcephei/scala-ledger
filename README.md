scala-ledger
============

A JVM library for reading and writing Ledger data files (http://www.ledger-cli.org/).

This is an extremely skeletal implementation right now, and assumes that you aren't using any of Ledger's advanced features.
Also, it assumes you're just using USD ("$") as the currency.

I wrote it to support another project of mine, but I'd be happy to maintain it if it's generally useful.

Build
-----

compile: ```./sbt compile```
test: ```./sbt test```
single jar: ```./sbt assembly```

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
