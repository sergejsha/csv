/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.Lexer.*

internal fun parseCsv(
    csvText: String,
    withHeaderRow: Boolean = true,
): Pair<CsvHeaderRow?, List<CsvDataRow>> {
    var pos = 0
    var lexer: Lexer = SimpleValue

    fun nextChar(): Char? {
        val nextPos = pos + 1
        return if (nextPos < csvText.length) csvText[nextPos] else null
    }

    var valueStarted = false
    val value = StringBuilder()
    val row = mutableListOf<String>()
    var header: CsvHeaderRow? = null
    val data = mutableListOf<CsvDataRow>()

    fun completeValue() {
        row.add(value.toString())
        value.clear()
        valueStarted = false
    }

    fun completeRow() {
        if (withHeaderRow && header == null) {
            header = row.toCsvHeaderRow()
        } else {
            data += row.toList()
        }
        row.clear()
    }

    while (pos < csvText.length) {
        val char = csvText[pos]
        lexer = when (lexer) {
            SimpleValue -> when (char) {
                '"' -> {
                    valueStarted = true
                    QuotedValue
                }
                ',' -> {
                    completeValue()
                    valueStarted = true
                    SimpleValue
                }
                '\r' -> {
                    if (nextChar() == '\n') {
                        pos++
                    }
                    if (valueStarted) {
                        completeValue()
                    }
                    if (row.isNotEmpty()) {
                        completeRow()
                    }
                    valueStarted = false
                    SimpleValue
                }
                '\n' -> {
                    if (valueStarted) {
                        completeValue()
                    }
                    if (row.isNotEmpty()) {
                        completeRow()
                    }
                    SimpleValue
                }
                else -> {
                    value.append(char)
                    SimpleValue
                }
            }
            QuotedValue -> when (char) {
                '"' -> when (nextChar()) {
                    '"' -> {
                        pos++
                        value.append(char)
                        QuotedValue
                    }
                    else -> { // Quote closed, value complete
                        completeValue()
                        when (nextChar()) {
                            ',' -> {
                                pos++
                                valueStarted = true
                            }
                            '\r' -> {
                                pos++
                                if (nextChar() == '\n') {
                                    pos++
                                }
                                completeRow()
                            }
                            '\n' -> {
                                pos++
                                completeRow()
                            }
                            null -> {
                                completeRow()
                            }
                        }
                        SimpleValue
                    }
                }
                else -> {
                    value.append(char)
                    QuotedValue
                }
            }
        }
        pos++
    }

    if (valueStarted) {
        completeValue()
    }

    if (row.isNotEmpty()) {
        completeRow()
    }

    return header to data
}

private enum class Lexer {
    SimpleValue,
    QuotedValue,
}