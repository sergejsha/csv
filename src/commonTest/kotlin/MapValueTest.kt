/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class MapValueTest {

    @Test
    fun mapValue() {

        val givenCsvString =
            """
                NAME,CODE,PRICE,DESCRIPTION
                BURGUNDY PINK,4PK.GD-S7,100,Pink burgundy
                HILLS PINK,BVH.5-NAR.S8,120,Pink hills
                PACKAGING,PKG.1,0.55,Eyewear packages. Models: all
                DARK BLUE,EVG.6,101,"Matte stainless steel. 
                Line 2.
                Line 3"
            """.trimIndent()

        val csv = CsvWithHeader.parseCsvText(givenCsvString) as CsvWithHeader
        val code = csv.header.headerByName("CODE") as CsvHeader
        val description = csv.header.headerByName("DESCRIPTION") as CsvHeader
        val data = csv.data.map { row ->
            val codeValue = row[code]
            if (!codeValue.startsWith("PKG.")) {
                row.mapValue(description) { "" }
            } else {
                row
            }
        }

        val csv2 = CsvWithHeader(csv.header, data)
        println(csv2.toCsvText())

        assertEquals("", csv2.data[0][description.index])
        assertEquals("", csv2.data[1].getOrElse(description.index) { "" })
        assertEquals("Eyewear packages. Models: all", csv2.data[2][description.index])
    }
}
