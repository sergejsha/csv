/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

public class Csv(
    public val rows: List<List<String>>,
) {
    // Multiline issue: https://stackoverflow.com/questions/2668678/importing-csv-with-line-breaks-in-excel-2007
    public fun toCsvText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
    ): String = buildString {
        rows.forEach { row ->
            row.forEachIndexed { index, value ->
                val escapedValue = value.escapeCsvValue(escapeWhitespaces)
                append(escapedValue)
                if (index < (row.size - 1)) {
                    append(',')
                }
            }
            append(newLine.value)
        }
    }
}

public enum class NewLine(
    public val value: String,
) {
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
