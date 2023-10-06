/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

class Csv(
    val rows: List<List<String>>,
) {
    // Multiline issue: https://stackoverflow.com/questions/2668678/importing-csv-with-line-breaks-in-excel-2007
    fun toCvsText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
    ): String =
        rows.fold("") { acc, row ->
            val rowText =
                row
                    .fold("") { rowAcc, cell ->
                        val escapedCell = cell.escapeCvsCell(escapeWhitespaces)
                        "$rowAcc$escapedCell,"
                    }
                    .removeSuffix(",")
            "$acc$rowText${newLine.value}"
        }
}

enum class NewLine(val value: String) {
    LF("\n"),
    CRLF("\r\n")
}

// https://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules
private fun String.escapeCvsCell(
    escapeWhitespaces: Boolean,
): String =
    when {
        isEmpty() -> "\"\""
        contains(",") || contains("\n") || (escapeWhitespaces && contains(" ")) -> {
            val escapedQuoted = replace("\"", "\"\"")
            "\"${escapedQuoted}\""
        }
        else -> this
    }
