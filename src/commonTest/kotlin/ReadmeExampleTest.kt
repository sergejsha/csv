package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class ReadmeExampleTest {

    @Test
    fun buildCsvObject() {

        // (1) build csv
        val csv = buildCsv {
            header {
                value("Code")
                value("Name")
            }
            data {
                value("DE")
                value("Deutschland")
            }
            data {
                value("BY")
                value("Belarus")
            }
        } as CsvWithHeader

        val code = csv.header.headerByName("Code") as CsvHeader
        val name = csv.header.headerByName("Name") as CsvHeader

        assertEquals(code, CsvHeader(0, "Code"))
        assertEquals(name, CsvHeader(1, "Name"))
        assertEquals(csv.data[0][code], "DE")
        assertEquals(csv.data[0][name], "Deutschland")
        assertEquals(csv.data[1][code], "BY")
        assertEquals(csv.data[1][name], "Belarus")

        // (2) csv to text
        val csvText = csv.toCsvText()
        assertEquals("Code,Name\nDE,Deutschland\nBY,Belarus\n", csvText)

        // (3) parse csv text
        val csv2 = CsvWithHeader.parseCsvText(csvText) as CsvWithHeader

        assertEquals(csv.header, csv2.header)
        assertEquals(csv.data, csv2.data)
        assertEquals(csv.allRows, csv2.allRows)

        // (5) transform csv
        val allRows = csv.allRows.map { row ->
            row.mapValue(name) { value ->
                if (value == "Belarus") "Weißrussland" else value
            }
        }

        val csv3 = CsvWithHeader.fromLists(allRows) as CsvWithHeader
        assertEquals("Code,Name\nDE,Deutschland\nBY,Weißrussland\n", csv3.toCsvText())
    }
}