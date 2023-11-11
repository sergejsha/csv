/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import de.halfbit.csv.Lexer.*

public fun parseCsv(csvText: String): Csv {
    var pos = 0
    var lexer: Lexer = BeforeValue

    fun nextChar(): Char? {
        val nextPos = pos + 1
        return if (nextPos < csvText.length) csvText[nextPos] else null
    }

    val value = StringBuilder()
    val row = mutableListOf<String>()
    val rows = mutableListOf<List<String>>()

    fun completeValue() {
        row.add(value.toString())
        value.clear()
    }

    fun completeRow() {
        rows.add(row.toList())
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

    return Csv(rows)
}

private enum class Lexer {
    BeforeValue,
    InsideValue,
    InsideEscapedValue,
}
