/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

class Csv(
    val rows: List<List<String>>,
) {
    // Multiline issue: https://stackoverflow.com/questions/2668678/importing-csv-with-line-breaks-in-excel-2007
    fun toCsvText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
    ): String =
        rows.fold("") { acc, row ->
            val rowText =
                row
                    .fold("") { rowAcc, value ->
                        val escapedValue = value.escapeCsvValue(escapeWhitespaces)
                        "$rowAcc$escapedValue,"
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
private fun String.escapeCsvValue(
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
