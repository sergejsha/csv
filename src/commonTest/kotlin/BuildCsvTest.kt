package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class BuildCsvTest {

    @Test
    fun buildCsv() {

        val csv = buildCsv {
            row {
                value("NAME")
                value("CODE")
                value("DESCRIPTION")
            }
            row {
                value("BEVERLY HILLS BLACK")
                value("BVH.1-NAR.S8")
            }
            row {
                value("BOAVISTA BLACK GRADIENT")
                value("BVS.1-NAR.S5")
                value("")
            }
            row {
                value("GXP EYEWEAR PACKAGING")
                value("PKG.2")
                value("PU pouch for eyewear in black cardboard box")
            }
        }

        val header = csv.header
        checkNotNull(header)
        assertEquals(header.indexOf("NAME"), 0)
        assertEquals(header.indexOf("CODE"), 1)
        assertEquals(header.indexOf("DESCRIPTION"), 2)

        assertEquals(csv.data.size, 3)
        assertEquals("BEVERLY HILLS BLACK", csv.data[0].value("NAME"))
        assertEquals("BVH.1-NAR.S8", csv.data[0].value("CODE"))
        assertEquals("", csv.data[0].value("DESCRIPTION"))

        assertEquals("BOAVISTA BLACK GRADIENT", csv.data[1].value("NAME"))
        assertEquals("BVS.1-NAR.S5", csv.data[1].value("CODE"))
        assertEquals("", csv.data[1].value("DESCRIPTION"))

        assertEquals("GXP EYEWEAR PACKAGING", csv.data[2].value("NAME"))
        assertEquals("PKG.2", csv.data[2].value("CODE"))
        assertEquals("PU pouch for eyewear in black cardboard box", csv.data[2].value("DESCRIPTION"))
    }

    @Test
    fun mapCsvNotNull() {

        val csv = buildCsv {
            row {
                value("NAME")
                value("CODE")
                value("DESCRIPTION")
            }
            row {
                value("BEVERLY HILLS BLACK")
                value("BVH.1-NAR.S8")
            }
            row {
                value("BOAVISTA BLACK GRADIENT")
                value("BVS.1-NAR.S5")
                value("")
            }
            row {
                value("GXP EYEWEAR PACKAGING")
                value("PKG.2")
                value("PU pouch for eyewear in black cardboard box")
            }
        }

        val descriptions = csv.data.map {
            it.value("DESCRIPTION")
        }

        val expectedDescriptions = listOf("", "", "PU pouch for eyewear in black cardboard box")
        assertEquals(expectedDescriptions, descriptions)
    }
}