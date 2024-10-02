/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.BaseCsv.Row

/**
 * Object with comma-separated values stored as list of [Row]'s. Each row is a list
 * of strings. Empty values are empty strings.
 *
 * If your CSV-data has header row, use [BaseCsv] instead of this base type.
 */
public interface BaseCsv {
    public val allRows: List<Row>

    // Multiline issue: https://stackoverflow.com/questions/2668678/importing-csv-with-line-breaks-in-excel-2007
    public fun toCsvText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
    ): String = buildString {
        allRows.forEach { row ->
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

    public interface Row : List<String> {
        public fun replaceValue(valueIndex: Int, newValue: String): Row
    }
}

/**
 * CSV-object  by with a mandatory header row. It has more convenient methods
 * for working with columns by their names.
 */
public interface Csv : BaseCsv {
    public val header: HeaderRow
    public val data: List<DataRow>

    public interface HeaderRow : Row {
        public fun indexOfColumn(name: String): Int
    }

    public interface DataRow : Row {
        public fun value(columnName: String): String?
        public fun replaceValue(columnName: String, newValue: String): DataRow
    }

    public companion object {
        public fun fromText(csvText: String): Csv = parseCsv(csvText)

        public fun fromList(allRows: List<List<String>>): BaseCsv {
            return BaseCsv(allRows.map { DefaultRow(it) })
        }

        public fun fromList(header: List<String>, data: List<List<String>>): Csv {
            val headerRow = DefaultHeaderRow(header)
            return Csv(
                header = headerRow,
                data = data.map { DefaultDataRow(it, headerRow) },
            )
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
