/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

@DslMarker
public annotation class CsvDsl

public inline fun buildCsv(
    block: CsvBuilder.() -> Unit
): Csv {
    val headerRows = mutableListOf<CsvHeaderRow>()
    val dataRows = mutableListOf<CsvDataRow>()
    block(CsvBuilder(headerRows, dataRows))
    val headerRow = headerRows.getOrNull(0)
    return if (headerRow == null) {
        CsvNoHeader(dataRows)
    } else {
        CsvWithHeader(headerRow, dataRows)
    }
}

@CsvDsl
public class CsvBuilder @PublishedApi internal constructor(
    @PublishedApi internal val headerRows: MutableList<CsvHeaderRow>,
    @PublishedApi internal val dataRows: MutableList<CsvRow>,
) {
    public inline fun header(block: CsvDataHeaderBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        block(CsvDataHeaderBuilder(row))
        headerRows.clear()
        headerRows += row.toCsvHeaderRow()
    }

    public inline fun data(block: CsvDataRowBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        block(CsvDataRowBuilder(row))
        dataRows += row
    }
}

@CsvDsl
public class CsvDataHeaderBuilder @PublishedApi internal constructor(
    private val row: MutableList<String>,
) {
    public fun column(value: String) {
        row.add(value)
    }
}

@CsvDsl
public class CsvDataRowBuilder @PublishedApi internal constructor(
    private val row: MutableList<String>,
) {
    public fun value(value: String) {
        row.add(value)
    }
}

@PublishedApi
internal fun List<String>.toCsvHeaderRow(): CsvHeaderRow =
    CsvHeaderRow(
        mapIndexed { index, name ->
            CsvColumn(index, name)
        }
    )
