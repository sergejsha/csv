/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.BaseCsv.Row
import de.halfbit.csv.Csv.DataRow
import de.halfbit.csv.Csv.HeaderRow
import kotlin.math.max

@DslMarker
public annotation class CsvDsl

public fun buildCsv(
    block: CsvBuilder.() -> Unit
): Csv {
    val header = mutableListOf<HeaderRow>()
    val data = mutableListOf<DataRow>()
    block(CsvBuilder(header, data))
    val headerRow = header.getOrNull(0) ?: DefaultHeaderRow(emptyList())
    return Csv(headerRow, data)
}

@CsvDsl
public class CsvBuilder internal constructor(
    private val header: MutableList<HeaderRow>,
    private val data: MutableList<DataRow>,
) {
    public fun row(block: CsvRowBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        val scope = CsvRowBuilder(row)
        block(scope)
        if (header.isEmpty()) {
            header += DefaultHeaderRow(row)
        } else {
            data += DefaultDataRow(row, header[0])
        }
    }
}

@CsvDsl
public class CsvRowBuilder internal constructor(
    private val row: MutableList<String>,
) {
    public fun value(value: String) {
        row.add(value)
    }
}

// implementations

public fun BaseCsv(allRows: List<Row>): BaseCsv = object : BaseCsv {
    override val allRows: List<Row> get() = allRows
    override fun toString(): String =
        buildString {
            allRows.forEach { data ->
                append(data)
                append("\n")
            }
        }
}

public fun Csv(
    header: HeaderRow,
    data: List<DataRow>,
): Csv = object : Csv {
    override val header: HeaderRow get() = header
    override val data: List<DataRow> get() = data
    override val allRows: List<Row> by lazy {
        if (header.isEmpty()) data else listOf(header) + data
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

internal open class DefaultRow(protected val row: List<String>) : Row, List<String> by row {

    override fun replaceValue(valueIndex: Int, newValue: String): Row {
        val thisRow = this
        val newRow = buildList {
            for (index in 0..max(thisRow.size, valueIndex)) {
                if (index == valueIndex) {
                    add(newValue)
                } else {
                    add(thisRow.getOrNull(index) ?: "")
                }
            }
        }
        return DefaultRow(newRow)
    }

    override fun toString(): String = row.toString()
}

internal class DefaultHeaderRow(row: List<String>) : HeaderRow, DefaultRow(row) {
    private val indexOfColumn: MutableMap<String, Int> by lazy { mutableMapOf() }

    override fun indexOfColumn(name: String): Int =
        indexOfColumn[name] ?: run {
            val index = indexOf(name)
            indexOfColumn[name] = index
            index
        }
}

internal class DefaultDataRow(
    row: List<String>,
    private val header: HeaderRow
) : DataRow, DefaultRow(row) {

    override fun value(columnName: String): String =
        row.getOrNull(header.indexOfColumn(columnName)) ?: ""

    override fun replaceValue(columnName: String, newValue: String): DataRow {
        val replaceIndex = header.indexOfColumn(columnName)
        if (replaceIndex < 0) return this
        val newRow = buildList {
            for (index in 0..max(row.lastIndex, replaceIndex)) {
                if (index == replaceIndex) {
                    add(newValue)
                } else {
                    add(row.getOrNull(index) ?: "")
                }
            }
        }
        return DefaultDataRow(newRow, header)
    }
}
