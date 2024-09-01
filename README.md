[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)
![maintenance-status](https://img.shields.io/badge/maintenance-passively--maintained-yellowgreen.svg)

# 🗂 CSV ️

Small and fast CSV parser and generator written for one of my projects, and then shared with awesome Kotlin community.

![Architecture diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/sergejsha/csv/master/documentation/architecture.v1.iuml)

# Usage

There is no documentation for the API which consists of just three methods:
```kotlin

// Build CSV object using simple DSL
val csv = buildCsv {
    row {
        value("Code")
        value("Name")
    }
    row {
        value("DE")
        value("Germany")
    }
}

// Export CSV object to CSV text
val csvText = csv.toCsvText()

// Parse CSV text to get a CSV object
val csv2 = parseCsv(csvText)

// Compare created and parsed objects
csv == csv2 // true
```

Feel free to open PRs for features you miss, please remember keeping API minimalistic, predictable and self-explanatory.

# Dependencies

In `gradle/libs.versions.toml`
```toml
[versions]
kotlin = "2.0.20"
csv = "0.11"

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
Copyright 2023-2024 Sergej Shafarenka, www.halfbit.de

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
