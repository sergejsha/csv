/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class MapColumnTest {

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
        val code = csv.header.columnByName("CODE") as CsvColumn
        val description = csv.header.columnByName("DESCRIPTION") as CsvColumn

        val csv2 = csv.copy(
            data = csv.data.map { row ->
                if (!row[code].startsWith("PKG.")) {
                    row.mapValueOf(description) { "" }
                } else {
                    row
                }
            }
        )
        println(csv2.toCsvText())

        assertEquals("", csv2.data[0][description.index])
        assertEquals("", csv2.data[1].getOrElse(description.index) { "" })
        assertEquals("Eyewear packages. Models: all", csv2.data[2][description.index])
    }
}
