package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class ReplaceValueTest {

    @Test
    fun replaceValue() {

        val givenCsvString =
            """
                NAME,CODE,DESCRIPTION
                BURGUNDY PINK,4PK.GD-S7,Pink burgundy
                HILLS PINK,BVH.5-NAR.S8,Pink hills
                PACKAGING,PKG.1,"Models: A, B, C"
            """.trimIndent()

        val csv = Csv.parserText(givenCsvString)
        val data = csv.data
            .map {
                val code = it.code
                if (!code.startsWith("PKG.")) {
                    it.replaceValue("DESCRIPTION", "")
                } else it
            }

        val csv2 = Csv(csv.header, data)
        println(csv2)

        assertEquals("", csv2.data[0].description)
        assertEquals("", csv2.data[1].description)
        assertEquals("Models: A, B, C", csv2.data[2].description)
    }
}

private val Csv.DataRow.code: String
    get() = value("CODE") ?: ""

private val Csv.DataRow.description: String
    get() = value("DESCRIPTION") ?: ""