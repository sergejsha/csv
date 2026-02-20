# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform CSV parser and builder library (`de.halfbit:csv`). Small, fast, and convenient library for parsing and building CSV strings.

## Build Commands

```bash
# Build the project
./gradlew build

# Run all tests across all platforms
./gradlew allTests

# Run JVM tests only (fastest for development)
./gradlew jvmTest

# Run JS tests
./gradlew jsBrowserTest

# Run native tests (e.g., macOS)
./gradlew macosX64Test macosArm64Test

# Check for dependency updates
./gradlew dependencyUpdates
```

## Architecture

The library uses a sealed hierarchy with two main CSV types:

- **`Csv`** - Abstract base class with `allRows` (all rows including header) and `data` (data rows only)
- **`CsvWithHeader`** - CSV with a header row; provides column-by-name access
- **`CsvNoHeader`** - CSV without header; pure row-based access

Core types:
- **`CsvHeaderRow`** - List of `CsvColumn` with lookup by name or index
- **`CsvColumn`** - Column definition with `index` and `name`
- **`CsvRow`** / **`CsvDataRow`** - Typealiases for `List<String>`

Key operations:
- Parsing: `CsvWithHeader.fromCsvText()` / `CsvNoHeader.fromCsvText()`
- Building: `buildCsv { header { ... }; data { ... } }` DSL
- Output: `csv.toCsvText()` with configurable line endings

Parser implementation (`parseCsv.kt`) uses a simple state machine with `Lexer` enum (`SimpleValue`, `QuotedValue`).

## Code Conventions

- Package: `de.halfbit.csv`
- Kotlin `explicitApi()` is enabled - all public APIs must have explicit visibility modifiers
- JVM target: JDK 1.8
- Tests go in `src/commonTest/kotlin/`

## Release Process

1. Bump version in `build.gradle.kts`
2. Run: `./gradlew clean build releaseToMavenCentral`
3. Complete release at https://central.sonatype.com/publishing
