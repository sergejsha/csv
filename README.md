[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)
![maintenance-status](https://img.shields.io/badge/maintenance-passively--maintained-yellowgreen.svg)

# CSV

A tiny, fast Kotlin Multiplatform library for parsing and building CSV strings. Zero dependencies, explicit API, and predictable behavior.

**Platforms:** JVM, JS (Browser/Node), iOS, macOS, Linux, Windows

Contributions welcome — please keep the API minimalistic and self-explanatory.

# Architecture

<img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/sergejsha/csv/master/documentation/architecture.v3.iuml">

# Usage

### Build CSV

```kotlin
val csv = buildCsv {
    header {
        column("Code")
        column("Name")
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
```

### Access columns and values

```kotlin
val code = csv.header.columnByName("Code")!!  // CsvColumn(index=0, name="Code")
val name = csv.header.columnByName("Name")!!  // CsvColumn(index=1, name="Name")

csv.data[0][code]  // "DE"
csv.data[0][name]  // "Deutschland"
csv.data[1][code]  // "BY"
csv.data[1][name]  // "Belarus"
```

### Convert to CSV text

```kotlin
val text = csv.toCsvText()
// "Code,Name\nDE,Deutschland\nBY,Belarus\n"

// With CRLF line endings:
csv.toCsvText(newLine = NewLine.CRLF)
```

### Parse CSV text

```kotlin
// With header row:
val csv = CsvWithHeader.fromCsvText("Code,Name\nDE,Deutschland\n")

// Without header row:
val csv = CsvNoHeader.fromCsvText("DE,Deutschland\nBY,Belarus\n")
```

### Transform data

```kotlin
val transformed = csv.copy(
    data = csv.data.map { row ->
        row.mapValueOf(name) { value ->
            if (value == "Belarus") "Weißrussland" else value
        }
    }
)
```

# Dependencies

In `gradle/libs.versions.toml`

```toml
[versions]
kotlin = "2.3.0"
csv = "1.3"

[libraries]
csv = { module = "de.halfbit:csv", version.ref = "csv" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

In `shared/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.csv)
        }
    }
}
```

# Releasing

1. Bump version in `build.gradle.kts` of the root project
2. `./gradlew clean build releaseToMavenCentral`

# License

```
Copyright 2023-2026 Sergej Shafarenka, www.halfbit.de

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
