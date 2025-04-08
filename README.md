[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)
![maintenance-status](https://img.shields.io/badge/maintenance-passively--maintained-yellowgreen.svg)

Feel free to open PRs for features you miss, please remember keeping API minimalistic, predictable and self-explanatory.

# 🗂 CSV ️

Small, fast and convenient multiplatform CSV parser and builder written for one of my projects, and then shared with awesome Kotlin community.

![Architecture diagram](https://www.plantuml.com/plantuml/svg/VP9FQnin4CNl-XJ3dZPGB-tLb2KaERGGMgXBBwM7YJHUY-ZF8it6CMs_U-LPRJQqnRvO-_IRDuyPNMV6n9DtAZ_QC6923oFgHnnenoT7a4WpXi6HEbX3G7xa4tZo3vJoB6McH95FOqMqJRhWIZlC6Mumqsr-WfyAoW-T-nAFUGltDlFxSkn5vbYGh_JCxTKMTfni6DJlnM6jFX0QIblu8F-bMbI3ZDNmhXp5T2bUqMfXWw7dp_lRw2OVse2UIiUFWTUAwb7v9tGJjLhmJqBNV7ARQ7IewTjVq1i8T1InuDLlRL0-waaoptQzKdNiqDnCrXbAkQaPPhXvECjb6EtTxlwg0SomAA-fyjcDc3B9E6VpcHRrF_G-HNzEabl9ypfKFrTMGeQqz8JkkudCP7vqlQne2sEAPjAKLUrqaLayBp5gtTucsU70cr-zZ6_PqSRH_aKKmSHZE0iIjzYjXSjB53NE83Hlk-vTixwIVov3yx90TKt1JDxz0G00)

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
val csv2 = Csv.parseText(csvText)

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

# Release Notes

- 0.16 Update to Kotlin 2.1.20
- 0.15 Add macosArm64 target
- 0.14 Add linuxArm64 target, update to Kotlin 2.0.21
- 0.13 Some API changes and DataRow.replaceValue(), see PR #13 for details
- 0.12 Add HeaderRow and DataRow types, enabling easier data transformations
- 0.11 Update to Kotlin 2.0.20
- 0.10 Migrate to the Apache 2.0 license
- 0.9 Improve performance of CSV generation (PR #4)
- 0.8 Update to Kotlin 2.0.0
- 0.7 Fixed issue #1
- 0.6 Fixed parsing of CRLF files, update to Kotlin 1.9.22
- 0.5 Updated to Kotlin 1.9.20
- 0.4 Fixed description of maven artifacts 
- 0.3 Fixed typos in method names
- 0.2 Initial release

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
