/** Copyright 2023-2025 Halfbit GmbH, Sergej Shafarenka */
package de.halfbit.csv

/**
 * Represents CSV data with a header row.
 *
 * @property header the header row of the CSV
 * @property data the data rows
 */
public data class CsvWithHeader(
    public val header: CsvHeaderRow,
    public override val data: List<CsvDataRow>,
) : Csv(listOf(header.map { it.name }) + data) {

    override fun toString(): String =
        buildString {
            append(header)
            append("\n")
            data.forEach { data ->
                append(data)
                append("\n")
            }
        }

    public companion object {

        /**
         * Parses CSV text into a [CsvWithHeader] instance.
         *
         * @param csvText the CSV-formatted string
         * @return a [CsvWithHeader] if parsing succeeds and the header row is present, or `null` otherwise.
         */
        public fun fromCsvText(csvText: String): CsvWithHeader? {
            val (header, data) = parseCsv(csvText, true)
            return if (header == null) null else {
                CsvWithHeader(header, data)
            }
        }

        /**
         * Creates a [CsvWithHeader] from a list of rows, where the first row is the header.
         *
         * @param allRows all rows, with the first row as header
         * @return a [CsvWithHeader] if header is present, or `null` otherwise.
         */
        public fun fromLists(allRows: List<List<String>>): CsvWithHeader? {
            val headerRowNames = allRows.getOrNull(0)
            return if (headerRowNames == null) null else {
                val headerRow = headerRowNames.toCsvHeaderRow()
                CsvWithHeader(headerRow, allRows.subList(1, allRows.size))
            }
        }

        /**
         * Creates a [CsvWithHeader] from a list of header and data rows.
         *
         * @param header the header rows
         * @param header data rows
         * @return a [CsvWithHeader] if header row is not empty, or `null` otherwise.
         */
        public fun fromLists(header: List<String>, data: List<List<String>>): CsvWithHeader? {
            return if (header.isEmpty()) null else {
                val headerRow = header.toCsvHeaderRow()
                CsvWithHeader(headerRow, data)
            }
        }
    }
}

/**
 * Represents the header row of a CSV file, containing column definitions.
 *
 * @constructor Creates a header row from a list of [CsvColumn]s.
 * @property columns list of columns in the header row
 */
public data class CsvHeaderRow(
    @PublishedApi internal val columns: List<CsvColumn>,
) : List<CsvColumn> by columns {
    private val columnsByName = columns.associateBy { it.name }

    /** Returns the [CsvColumn] with the given [name], or `null` if not found. */
    public fun columnByName(name: String): CsvColumn? = columnsByName[name]

    /** Returns the [CsvColumn] at the given [index], or `null` if out of bounds. */
    public fun columnByIndex(index: Int): CsvColumn? = columns.getOrNull(index)
}

/**
 * Represents a column in a [CsvHeaderRow] of a CSV file.
 *
 * @property index Zero-based index of the column inside its [CsvHeaderRow].
 * @property name Name (label) of the column as defined in [CsvHeaderRow].
 */
public data class CsvColumn(

    /** Zero-based index of the column inside its [CsvHeaderRow]. */
    public val index: Int,

    /** Name (label) of the column as it is defined in [CsvHeaderRow]. */
    public val name: String,
)

/**
 * Returns the value from this row corresponding to the given [column].
 *
 * @param column the column to retrieve the value for
 * @return the value at the column's index
 */
public operator fun CsvDataRow.get(column: CsvColumn): String =
    get(column.index)

/**
 * Returns the value from this row for the given [column], or `null` if
 * the index is out of bounds.
 *
 * @param column the column to retrieve the value for
 * @return the value at the column's index, or `null` if not present
 */
public fun CsvDataRow.getOrNull(column: CsvColumn): String? =
    getOrNull(column.index)

/**
 * Returns the value from this row for the given [column], or an
 * empty string if not present.
 *
 * @param column the column to retrieve the value for
 * @return the value at the column's index, or an empty string if not present
 */
public fun CsvDataRow.getOrEmpty(column: CsvColumn): String? =
    getOrNull(column.index) ?: ""

/**
 * Returns the value from this row for the given [column], or computes a
 * default value if not present.
 *
 * @param column the column to retrieve the value for
 * @param defaultValue function to compute a default value if the column is
 * missing from the dataset
 * @return the value at the column's index, or the result of [defaultValue]
 */
public inline fun CsvDataRow.getOrElse(column: CsvColumn, defaultValue: (CsvColumn) -> String): String =
    getOrElse(column.index) { defaultValue(column) }

/**
 * Applies [transform] to the value at the given [column], returning
 * a new list with the transformed value.
 *
 * @param column the column whose value will be transformed
 * @param transform function to apply to the value
 * @return a new list with the transformed value at the specified column
 *
 * Usage example: replace value "Belarus" with "Weißrussland" in the "Name" column
 * ```
 * val name = csv.header.columnByName("Name") as CsvColumn
 * val transformedCsv = csv.copy(
 *   data = csv.data.map { row ->
 *     row.mapValueOf(name) { value ->
 *       if (value == "Belarus") "Weißrussland" else value
 *     }
 *   }
 * )
 * ```
 */
public inline fun CsvDataRow.mapValueOf(column: CsvColumn, transform: (String) -> String): List<String> =
    mapIndexed { index, value ->
        if (index == column.index) {
            transform(value)
        } else {
            value
        }
    }

public inline fun CsvDataRow.mapValue2(header: CsvHeaderRow, transform: (CsvColumn, String) -> String): CsvDataRow =
    header.columns.map { column ->
        transform(column, getOrElse(column) { "" })
    }
