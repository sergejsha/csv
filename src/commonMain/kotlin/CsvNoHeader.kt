/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

/**
 * Represents CSV data without a header row.
 *
 * @property data the data rows
 */
public data class CsvNoHeader(
    public override val data: List<CsvDataRow>,
) : Csv(data) {

    override fun toString(): String =
        buildString {
            data.forEach { data ->
                append(data)
                append("\n")
            }
        }

    public companion object {

        /**
         * Parses CSV text into a [CsvNoHeader] instance.
         *
         * @param csvText the CSV-formatted string
         * @return a [CsvNoHeader] instance
         */
        public fun parseCsvText(csvText: String): CsvNoHeader {
            val (_, data) = parseCsv(csvText, false)
            return CsvNoHeader(data)
        }

        /**
         * Creates a [CsvNoHeader] from a list of rows.
         *
         * @param allRows all data rows
         * @return a [CsvNoHeader] instance
         */
        public fun fromLists(allRows: List<List<String>>): CsvNoHeader {
            return CsvNoHeader(allRows)
        }
    }
}
