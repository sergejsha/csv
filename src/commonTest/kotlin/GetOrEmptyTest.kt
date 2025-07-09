/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetOrEmptyTest {

    @Test
    fun get_or_empty_returns_value() {

        val givenCsvString = "date,amount,user_id,id\r\n2025-04-14,250.00,2,2\r\n2025-04-15,100.50,1,1\r\n"
        val csv = CsvWithHeader.fromCsvText(givenCsvString) as CsvWithHeader
        val amountColumn = csv.header.columnByName("amount")
        assertNotNull(amountColumn)

        val amount = csv.data[0].getOrEmpty(amountColumn)
        assertEquals("250.00", amount)
    }

    @Test
    fun get_or_empty_returns_empty() {

        val givenCsvString = "date,amount,user_id,id\r\n2025-04-14,250.00,2\r\n2025-04-15,100.50,1\r\n"
        val csv = CsvWithHeader.fromCsvText(givenCsvString) as CsvWithHeader
        val idColumn = csv.header.columnByName("id")
        assertNotNull(idColumn)

        val id1 = csv.data[0].getOrEmpty(idColumn)
        assertEquals("", id1)

        val id2 = csv.data[1].getOrEmpty(idColumn)
        assertEquals("", id2)
    }
}
