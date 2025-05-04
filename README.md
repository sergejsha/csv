[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)
![maintenance-status](https://img.shields.io/badge/maintenance-passively--maintained-yellowgreen.svg)

Feel free to open PRs for features you miss, please remember keeping API minimalistic, predictable and self-explanatory.

# 🗂 CSV ️

Small, fast and convenient multiplatform CSV parser and builder written for one of my projects, and then shared with awesome Kotlin community.

# Architecture

<img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/sergejsha/csv/master/documentation/architecture.v2.iuml">

# Usage

Here is what you can do with the library:
```kotlin

// (1) Build a CSV file using simple DSL
val csv = buildCsv {
    row {
        value("Code")
        value("Name")
    }
    row {
        value("DE")
        value("Germany")
    }
    row {
        value("BY")
        value("Belarus")
    }
}

// (2) Export a CSV object to a CSV string
val csvText = csv.toCsvText()

// (3) Parse a CSV string to get a CSV object
val csv2 = Csv.parseCsvText(csvText)

// There are the data structures supported by the library 
val allRows: List<Row> = csv2.rows
val header: HeaderRow = csv2.header
val data: List<DataRow> = csv2.data

// (4) Transform CSV data
val codes = data.map { it.value("Code") } // ["DE", "BY"]
val names = data.map { it.value("Name") } // ["Germany", "Belarus"]
```

# Dependencies

In `gradle/libs.versions.toml`
```toml
[versions]
kotlin = "2.1.20"
csv = "0.16"

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
