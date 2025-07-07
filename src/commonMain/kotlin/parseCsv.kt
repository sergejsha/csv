/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.Lexer.*

internal fun parseCsv(
    csvText: String,
    withHeaderRow: Boolean = true,
): Pair<CsvHeaderRow?, List<CsvDataRow>> {
    var pos = 0
    var lexer: Lexer = BeforeValue

    fun nextChar(): Char? {
        val nextPos = pos + 1
        return if (nextPos < csvText.length) csvText[nextPos] else null
    }

    val value = StringBuilder()
    val row = mutableListOf<String>()
    var header: CsvHeaderRow? = null
    val data = mutableListOf<CsvDataRow>()

    fun completeValue() {
        row.add(value.toString())
        value.clear()
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
            BeforeValue -> when (char) {
                '"' -> InsideEscapedValue
                ',' -> {
                    completeValue()
                    BeforeValue
                }
                '\n' -> {
                    if (row.isNotEmpty()) {
                        completeRow()
                    }
                    BeforeValue
                }
                else -> {
                    value.append(char)
                    InsideValue
                }
            }
            InsideValue -> when (char) {
                ',' -> {
                    completeValue()
                    BeforeValue
                }
                '\r' -> {
                    if (nextChar() == '\n') {
                        pos++
                    }
                    completeValue()
                    completeRow()
                    BeforeValue
                }
                '\n' -> {
                    completeValue()
                    completeRow()
                    BeforeValue
                }
                else -> {
                    value.append(char)
                    when (nextChar()) {
                        ',' -> {
                            pos++
                            completeValue()
                            when (nextChar()) {
                                null -> { // EOF
                                    completeValue()
                                    completeRow()
                                }
                            }
                            BeforeValue
                        }
                        '\r' -> {
                            pos++
                            when (nextChar()) {
                                '\n' -> {
                                    pos++
                                }
                            }
                            completeValue()
                            completeRow()
                            BeforeValue
                        }
                        '\n' -> {
                            pos++
                            completeValue()
                            completeRow()
                            BeforeValue
                        }
                        null -> {
                            completeValue()
                            completeRow()
                            BeforeValue
                        }
                        else -> {
                            InsideValue
                        }
                    }
                }
            }
            InsideEscapedValue -> when (char) {
                '"' -> when (nextChar()) {
                    '"' -> {
                        pos++
                        value.append(char)
                        InsideEscapedValue
                    }
                    else -> {
                        completeValue()
                        when (nextChar()) {
                            ',' -> {
                                pos++
                            }
                            null -> {
                                completeRow()
                            }
                        }
                        BeforeValue
                    }
                }
                else -> {
                    value.append(char)
                    InsideEscapedValue
                }
            }
        }
        pos++
    }

    if (value.isNotEmpty()) {
        completeValue()
    }

    if (row.isNotEmpty()) {
        completeRow()
    }

    return header to data
}

private enum class Lexer {
    BeforeValue,
    InsideValue,
    InsideEscapedValue,
}
