/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

@DslMarker
annotation class CsvDsl

fun buildCsv(
    block: CsvBuilder.() -> Unit
): Csv {
    val rows = mutableListOf<List<String>>()
    block(CsvBuilder(rows))
    return Csv(rows)
}

@CsvDsl
class CsvBuilder internal constructor(
    private val rows: MutableList<List<String>>,
) {
    fun row(block: CsvRowBuilder.() -> Unit) {
        val row = mutableListOf<String>()
        val scope = CsvRowBuilder(row)
        block(scope)
        rows.add(row)
    }
}

@CsvDsl
class CsvRowBuilder internal constructor(
    private val row: MutableList<String>,
) {
    fun value(value: String) {
        row.add(value)
    }
}
