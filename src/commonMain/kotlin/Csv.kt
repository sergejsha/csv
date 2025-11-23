/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

/**
 * Base class for CSV data, containing all rows of the CSV file.
 *
 * @property allRows all rows in the CSV file, including header if present
 */
public abstract class Csv(
    public val allRows: List<CsvRow>,
) {
    public abstract val data: List<CsvDataRow>

    /**
     * Converts the CSV data to a CSV-formatted string.
     *
     * @param newLine the line separator to use
     * @param escapeWhitespaces whether to escape whitespaces in values
     * @param trailingNewLine whether to add a newline character after the last row
     * @return the CSV-formatted string representation
     */
    public fun toCsvText(
        newLine: NewLine = NewLine.LF,
        escapeWhitespaces: Boolean = false,
        trailingNewLine: Boolean = false,
    ): String = buildString {
        allRows.forEachIndexed { index, row ->
            row.forEachIndexed { index, value ->
                val escapedValue = value.escapeCsvValue(escapeWhitespaces)
                append(escapedValue)
                if (index < row.lastIndex) {
                    append(',')
                }
            }
            if (index != allRows.lastIndex || trailingNewLine) {
                append(newLine.value)
            }
        }
    }
}

/** Represents a single row in a CSV file as a list of string values. */
public typealias CsvRow = List<String>

/** Represents a data row in a CSV file, excluding the header row. */
public typealias CsvDataRow = List<String>

/**
 * Specifies the line separator used in CSV output.
 *
 * @property value the string value of the line separator
 */
public enum class NewLine(
    public val value: String,
) {
    /** Line feed as the line terminator */
    LF("\n"),

    /** Carriage Return + Line feed as the line terminator */
    CRLF("\r\n"),

    /** Carriage Return as the line terminator */
    CR("\r")
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
