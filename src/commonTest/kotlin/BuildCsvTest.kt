/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BuildCsvTest {

    @Test
    fun buildCsv() {

        val csv = buildCsv {
            header {
                value("NAME")
                value("CODE")
                value("DESCRIPTION")
            }
            data {
                value("BEVERLY HILLS BLACK")
                value("BVH.1-NAR.S8")
            }
            data {
                value("BOAVISTA BLACK GRADIENT")
                value("BVS.1-NAR.S5")
                value("")
            }
            data {
                value("GXP EYEWEAR PACKAGING")
                value("PKG.2")
                value("PU pouch for eyewear in black cardboard box")
            }
        } as CsvWithHeader

        val header = csv.header

        val name = header.columnByName("NAME") as CsvColumn
        assertEquals(CsvColumn(0, "NAME"), name)

        val code = header.columnByName("CODE") as CsvColumn
        assertEquals(CsvColumn(1, "CODE"), code)

        val description = header.columnByName("DESCRIPTION") as CsvColumn
        assertEquals(CsvColumn(2, "DESCRIPTION"), description)
        assertEquals(csv.data.size, 3)

        assertEquals("BEVERLY HILLS BLACK", csv.data[0].getOrEmpty(name))
        assertEquals("BVH.1-NAR.S8", csv.data[0].getOrEmpty(code))
        assertNull(csv.data[0].getOrNull(description))

        assertEquals("BOAVISTA BLACK GRADIENT", csv.data[1].getOrEmpty(name))
        assertEquals("BVS.1-NAR.S5", csv.data[1].getOrEmpty(code))
        assertEquals("", csv.data[1].get(description))

        assertEquals("GXP EYEWEAR PACKAGING", csv.data[2].getOrEmpty(name))
        assertEquals("PKG.2", csv.data[2].getOrEmpty(code))
        assertEquals("PU pouch for eyewear in black cardboard box", csv.data[2].get(description))
    }

    @Test
    fun mapCsvNotNull() {

        val csv = buildCsv {
            header {
                value("NAME")
                value("CODE")
                value("DESCRIPTION")
            }
            data {
                value("BEVERLY HILLS BLACK")
                value("BVH.1-NAR.S8")
            }
            data {
                value("BOAVISTA BLACK GRADIENT")
                value("BVS.1-NAR.S5")
                value("")
            }
            data {
                value("GXP EYEWEAR PACKAGING")
                value("PKG.2")
                value("PU pouch for eyewear in black cardboard box")
            }
        } as CsvWithHeader

        val description = csv.header.columnByName("DESCRIPTION") as CsvColumn
        val descriptions = csv.data.map { it.getOrNull(description) }
        val expectedDescriptions = listOf(null, "", "PU pouch for eyewear in black cardboard box")
        assertEquals(expectedDescriptions, descriptions)
    }
}