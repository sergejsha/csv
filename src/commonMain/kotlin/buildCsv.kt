/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.Csv.*

@DslMarker
public annotation class CsvDsl

public fun buildCsv(
    block: CsvBuilder.() -> Unit
): Csv {
    val header = mutableListOf<HeaderRow>()
    val data = mutableListOf<DataRow>()
    block(CsvBuilder(header, data))
    return Csv(header.getOrNull(0), data)
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
            header += HeaderRow(row)
        } else {
            data += DataRow(row, header[0])
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

internal fun HeaderRow(row: List<String>): HeaderRow =
    object : HeaderRow, List<String> by row {
        private val indexOfColumn: MutableMap<String, Int> by lazy { mutableMapOf() }
        override fun indexOfColumn(name: String): Int =
            indexOfColumn[name] ?: run {
                val index = indexOf(name)
                indexOfColumn[name] = index
                index
            }
        override fun toString(): String =
            row.toString()
    }

internal fun DataRow(row: List<String>, header: HeaderRow): DataRow =
    object : DataRow, List<String> by row {
        override fun value(columnName: String): String? =
            row.getOrNull(header.indexOfColumn(columnName))
        override fun toString(): String =
            row.toString()
    }
