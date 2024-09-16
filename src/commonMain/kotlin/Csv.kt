/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

public class Csv(
    public val header: HeaderRow?,
    public val data: List<DataRow>,
) {
    public val rows: List<Row> by lazy {
        buildList {
            header?.let { add(it) }
            addAll(data)
        }
    }

    public sealed interface Row : List<String>

    public interface HeaderRow : Row {
        public fun indexOfColumn(name: String): Int
    }

    public interface DataRow : Row {
        public fun value(columnName: String): String?
    }

    // Multiline issue: https://stackoverflow.com/questions/2668678/importing-csv-with-line-breaks-in-excel-2007
    public fun toCsvText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
    ): String = buildString {
        rows.forEach { row ->
            row.forEachIndexed { index, value ->
                val escapedValue = value.escapeCsvValue(escapeWhitespaces)
                append(escapedValue)
                if (index < row.lastIndex) {
                    append(',')
                }
            }
            append(newLine.value)
        }
    }

    override fun toString(): String =
        buildString {
            append(header)
            append("\n")
            data.forEach { data ->
                append(data)
                append("\n")
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
