/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

public typealias CsvRow = List<String>
public typealias CsvDataRow = List<String>

public operator fun CsvDataRow.get(header: CsvHeader): String =
    get(header.index)

public fun CsvDataRow.getOrNull(header: CsvHeader): String? =
    getOrNull(header.index)

public fun CsvDataRow.getOrEmpty(header: CsvHeader): String? =
    getOrNull(header.index) ?: ""

public inline fun CsvDataRow.getOrElse(header: CsvHeader, defaultValue: (CsvHeader) -> String): String =
    getOrElse(header.index) { defaultValue(header) }

public inline fun CsvDataRow.mapValue(header: CsvHeader, transform: (String) -> String): List<String> {
    return mapIndexed { index, value ->
        if (index == header.index) {
            transform(value)
        } else {
            value
        }
    }
}

public data class CsvHeader(
    public val index: Int,
    public val name: String,
)

public class CsvHeaderRow(
    private val headers: List<CsvHeader>,
) : List<CsvHeader> by headers {
    private val headersByName = headers.associateBy { it.name }
    public fun headerByName(name: String): CsvHeader? = headersByName[name]
    public fun headerByIndex(index: Int): CsvHeader? = headers.getOrNull(index)
}

public abstract class Csv(
    public val allRows: List<CsvRow>,
) {
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
}

public class CsvWithHeader(
    public val header: CsvHeaderRow,
    public val data: List<CsvDataRow>,
) : Csv(listOf(header.map { it.name }) + data) {

    override fun toString(): String =
        buildString {
            append(header)
            append("\n")
            data.forEach { data ->
                append(data)
                append("\n")
            }
        }

    public companion object {
        public fun parseCsvText(csvText: String): CsvWithHeader? {
            val (header, data) = parseCsv(csvText, true)
            return if (header == null) null else {
                CsvWithHeader(header, data)
            }
        }

        public fun fromLists(allRows: List<List<String>>): CsvWithHeader? {
            val headerRowNames = allRows.getOrNull(0)
            return if (headerRowNames == null) null else {
                val headerRow = headerRowNames.toCsvHeaderRow()
                CsvWithHeader(headerRow, allRows.subList(1, allRows.size))
            }
        }
    }
}

public class CsvNoHeader(
    public val data: List<CsvDataRow>,
) : Csv(data) {

    override fun toString(): String =
        buildString {
            data.forEach { data ->
                append(data)
                append("\n")
            }
        }

    public companion object {
        public fun parseCsvText(csvText: String): CsvNoHeader {
            val (_, data) = parseCsv(csvText, false)
            return CsvNoHeader(data)
        }

        public fun fromLists(allRows: List<List<String>>): CsvNoHeader {
            return CsvNoHeader(allRows)
        }
    }
}

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
