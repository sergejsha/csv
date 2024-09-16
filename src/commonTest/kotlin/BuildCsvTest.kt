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
        assertEquals(csv.data[0].value("NAME"), "BEVERLY HILLS BLACK")
        assertEquals(csv.data[0].value("CODE"), "BVH.1-NAR.S8")
        assertEquals(csv.data[0].value("DESCRIPTION"), null)

        assertEquals(csv.data[1].value("NAME"), "BOAVISTA BLACK GRADIENT")
        assertEquals(csv.data[1].value("CODE"), "BVS.1-NAR.S5")
        assertEquals(csv.data[1].value("DESCRIPTION"), "")

        assertEquals(csv.data[2].value("NAME"), "GXP EYEWEAR PACKAGING")
        assertEquals(csv.data[2].value("CODE"), "PKG.2")
        assertEquals(csv.data[2].value("DESCRIPTION"), "PU pouch for eyewear in black cardboard box")
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

        val descriptions = csv.data.mapNotNull {
            it.value("DESCRIPTION")
        }

        assertEquals(listOf("", "PU pouch for eyewear in black cardboard box"), descriptions)
    }
}