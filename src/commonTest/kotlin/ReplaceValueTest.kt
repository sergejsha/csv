package de.halfbit.csv

import de.halfbit.csv.Csv.Companion.parserCsvText
import kotlin.test.Test
import kotlin.test.assertEquals

class ReplaceValueTest {

    @Test
    fun replaceValue() {

        val givenCsvString =
            """
                NAME,CODE,PRICE,DESCRIPTION
                BURGUNDY PINK,4PK.GD-S7,100,Pink burgundy
                HILLS PINK,BVH.5-NAR.S8,120Pink hills
                PACKAGING,PKG.1,0.55,Eyewear packages. Models: all
                DARK BLUE,EVG.6,101,"Matte stainless steel. 
                Line 2.
                Line 3"
            """.trimIndent()

        val csv = parserCsvText(givenCsvString)
        val data = csv.data
            .map {
                val code = it.code
                if (!code.startsWith("PKG.")) {
                    it.replaceValue("DESCRIPTION", "")
                } else it
            }

        val csv2 = Csv(csv.header, data)
        println(csv2.toCsvText())

        assertEquals("", csv2.data[0].description)
        assertEquals("", csv2.data[1].description)
        assertEquals("Eyewear packages. Models: all", csv2.data[2].description)
    }
}

private val Csv.DataRow.code: String
    get() = value("CODE")

private val Csv.DataRow.description: String
    get() = value("DESCRIPTION")