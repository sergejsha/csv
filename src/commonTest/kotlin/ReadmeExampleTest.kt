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

        val code = csv.header.columnByName("Code") as CsvColumn
        val name = csv.header.columnByName("Name") as CsvColumn

        assertEquals(code, CsvColumn(0, "Code"))
        assertEquals(name, CsvColumn(1, "Name"))
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

        // (4) transform csv
        val csv3 = csv.copy(
            data = csv.data.map { row ->
                row.mapValueOf(name) { value ->
                    if (value == "Belarus") "Weißrussland" else value
                }
            }
        )
        assertEquals("Code,Name\nDE,Deutschland\nBY,Weißrussland\n", csv3.toCsvText())
    }
}