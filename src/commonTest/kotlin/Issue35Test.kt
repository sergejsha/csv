/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class Issue35Test {

    @Test
    fun parseTrailingEmptyFields() {

        val given =
            """
                year,make,model,price
                1997,Ford,E350,3000.00
                1999,Chevy,Venture,
                2001,VW,,
            """.trimIndent()

        val csv = CsvWithHeader.fromCsvText(given) as CsvWithHeader
        val actual = csv.toCsvText()
        val expected = """
            year,make,model,price
            1997,Ford,E350,3000.00
            1999,Chevy,Venture,""
            2001,VW,"",""
        """.trimIndent()

        assertEquals(expected, actual)
    }
}
