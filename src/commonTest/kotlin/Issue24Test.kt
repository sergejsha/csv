/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Issue24Test {

    @Test
    fun rn_can_be_used_as_a_line_terminator() {

        val givenCsvString = "date,amount,user_id,id\r\n2025-04-14,250.00,2,2\r\n2025-04-15,100.50,1,1\r\n"
        val csv = CsvWithHeader.fromCsvText(givenCsvString) as CsvWithHeader

        assertEquals(3, csv.allRows.size)
        assertContentEquals(listOf("date", "amount", "user_id", "id"), csv.allRows[0])
        assertContentEquals(listOf("2025-04-14", "250.00", "2", "2"), csv.allRows[1])
        assertContentEquals(listOf("2025-04-15", "100.50", "1", "1"), csv.allRows[2])
    }

    @Test
    fun r_can_be_used_as_a_line_terminator() {

        val givenCsvString = "date,amount,user_id,id\r2025-04-14,250.00,2,2\r2025-04-15,100.50,1,1\r"
        val csv = CsvWithHeader.fromCsvText(givenCsvString) as CsvWithHeader

        assertEquals(3, csv.allRows.size)
        assertContentEquals(listOf("date", "amount", "user_id", "id"), csv.allRows[0])
        assertContentEquals(listOf("2025-04-14", "250.00", "2", "2"), csv.allRows[1])
        assertContentEquals(listOf("2025-04-15", "100.50", "1", "1"), csv.allRows[2])
    }

    @Test
    fun last_row_can_have_no_line_terminators() {

        val givenCsvString = "date,amount,user_id,id\r\n2025-04-14,250.00,2,2"
        val csv = CsvWithHeader.fromCsvText(givenCsvString) as CsvWithHeader

        assertEquals(2, csv.allRows.size)
        assertContentEquals(listOf("date", "amount", "user_id", "id"), csv.allRows[0])
        assertContentEquals(listOf("2025-04-14", "250.00", "2", "2"), csv.allRows[1])
    }
}
