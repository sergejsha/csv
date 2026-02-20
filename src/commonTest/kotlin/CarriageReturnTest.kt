/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for proper handling of carriage return (\r) characters in CSV values.
 * According to RFC 4180, fields containing line breaks (CRLF) should be enclosed in double quotes.
 */
class CarriageReturnTest {

    @Test
    fun valueWithCarriageReturnIsQuoted() {
        // Value containing \r should be quoted in output
        val csv = CsvNoHeader.fromLists(listOf(
            listOf("value\rwithCR", "normal")
        ))
        val csvText = csv.toCsvText().trim()

        // Expected: quoted value with \r
        assertEquals("\"value\rwithCR\",normal", csvText)
    }

    @Test
    fun valueWithCrLfIsQuoted() {
        // Value containing \r\n should be quoted in output
        val csv = CsvNoHeader.fromLists(listOf(
            listOf("line1\r\nline2", "normal")
        ))
        val csvText = csv.toCsvText().trim()

        // Expected: quoted value with \r\n
        assertEquals("\"line1\r\nline2\",normal", csvText)
    }

    @Test
    fun valueWithOnlyLfIsQuoted() {
        // Value containing only \n should be quoted (existing behavior)
        val csv = CsvNoHeader.fromLists(listOf(
            listOf("line1\nline2", "normal")
        ))
        val csvText = csv.toCsvText().trim()

        // Expected: quoted value with \n
        assertEquals("\"line1\nline2\",normal", csvText)
    }

    @Test
    fun parseValueWithCarriageReturn() {
        // Parse quoted value containing \r
        val givenCsvString = "\"value\rwithCR\",normal"
        val csv = CsvNoHeader.fromCsvText(givenCsvString)

        assertEquals(1, csv.data.size)
        assertEquals(2, csv.data[0].size)
        assertEquals("value\rwithCR", csv.data[0][0])
        assertEquals("normal", csv.data[0][1])
    }

    @Test
    fun roundTripValueWithCarriageReturn() {
        // Full round-trip: create CSV with \r, convert to text, parse back
        val original = CsvNoHeader.fromLists(listOf(
            listOf("value\rwithCR", "normal"),
            listOf("another", "value\r\nwith\rmultiple\rcr")
        ))

        val csvText = original.toCsvText()
        val parsed = CsvNoHeader.fromCsvText(csvText)

        assertEquals(original.data, parsed.data)
    }

    @Test
    fun roundTripValueWithCrLf() {
        // Full round-trip: create CSV with \r\n, convert to text, parse back
        val original = CsvNoHeader.fromLists(listOf(
            listOf("line1\r\nline2", "normal")
        ))

        val csvText = original.toCsvText()
        val parsed = CsvNoHeader.fromCsvText(csvText)

        assertEquals(original.data, parsed.data)
    }
}
