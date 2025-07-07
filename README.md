[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)
![maintenance-status](https://img.shields.io/badge/maintenance-passively--maintained-yellowgreen.svg)

Feel free to open PRs for features you miss, please remember keeping API minimalistic, predictable and self-explanatory.

# 🗂 CSV ️

Small, fast and convenient multiplatform CSV parser and builder written for one of my projects, and then shared with awesome Kotlin community.

# Architecture

<img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/sergejsha/csv/master/documentation/architecture.v3.iuml">

# Usage

Here is what you can do with the library:
```kotlin

// (1) build csv
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
```

# Dependencies

In `gradle/libs.versions.toml`
```toml
[versions]
kotlin = "2.2.0"
csv = "1.0"

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
2. `./gradlew clean build publishAllPublicationsToCentralRepository`

# License
```
Copyright 2023-2025 Sergej Shafarenka, www.halfbit.de

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
