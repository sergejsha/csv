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
    public inline fun header(block: CsvRowBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        block(CsvRowBuilder(row))
        headerRows += row.toCsvHeaderRow()
    }

    public inline fun data(block: CsvRowBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        block(CsvRowBuilder(row))
        dataRows += row
    }
}

@CsvDsl
public class CsvRowBuilder @PublishedApi internal constructor(
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
            CsvHeader(index, name)
        }
    )
