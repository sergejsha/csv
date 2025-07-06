/** Copyright 2023 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Issue1Test {

    @Test
    fun commaAtEOFIsNotIgnored() {

        val givenCsvString =
            """
                year,make,model,price
                1997,Ford,E350,3000.00
                1999,Chevy,Venture,
            """.trimIndent()

        val csv = CsvWithHeader.parseCsvText(givenCsvString) as CsvWithHeader
        assertEquals(3, csv.allRows.size)
        assertContentEquals(listOf("1999", "Chevy", "Venture", ""), csv.allRows[2])
    }
}