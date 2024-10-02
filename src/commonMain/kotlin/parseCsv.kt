/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.Csv.DataRow
import de.halfbit.csv.Csv.HeaderRow
import de.halfbit.csv.Lexer.*

internal fun parseCsv(csvText: String): Csv {
    var pos = 0
    var lexer: Lexer = BeforeValue

    fun nextChar(): Char? {
        val nextPos = pos + 1
        return if (nextPos < csvText.length) csvText[nextPos] else null
    }

    val value = StringBuilder()
    val row = mutableListOf<String>()
    val header = mutableListOf<HeaderRow>()
    val data = mutableListOf<DataRow>()

    fun completeValue() {
        row.add(value.toString())
        value.clear()
    }

    fun completeRow() {
        if (header.isEmpty()) {
            header += DefaultHeaderRow(row.toList())
        } else {
            data += DefaultDataRow(row.toList(), header[0])
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
                            when (nextChar()) {
                                '\n' -> {
                                    pos++
                                    InsideValue
                                }
                                else -> {
                                    pos++
                                    completeValue()
                                    completeRow()
                                    BeforeValue
                                }
                            }
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

    val headerRow = header.getOrNull(0) ?: DefaultHeaderRow(emptyList())
    return Csv(headerRow, data)
}

private enum class Lexer {
    BeforeValue,
    InsideValue,
    InsideEscapedValue,
}
