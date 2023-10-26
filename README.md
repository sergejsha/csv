[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)

# 🗂 CSV ️

This tiny library was written for one of my projects, and then shared with awesome Kotlin community. 

# Usage

There is no documentation for the API which consists of just three methods:
```kotlin

// 1. Build CSV using mico DSL
val csv = buildCsv {
    row { value("Code"); value("Name") }
    row { value("DE"); value("Germany") }
}

// 2. Export CSV object to CSV text
val csvText = csv.toCsvText()

// 3. Parse CSV text into a CSV object
val csv2 = parseCsv(csvText)

csv == csv2 // true
```

Feel free to open PRs for features you miss, please remember though keeping API minimalistic, predictable and self-explanatory.

# Dependencies

In gradle/libs.versions.toml
```toml
[versions]
csv = "0.3"

[libraries]
csv = { module = "de.halfbit:csv", version.ref = "csv" }
```

In shared/build.gradle.kts
```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.csv)
            }
        }
    }
}
```

# Release

1. Bump version in `build.gradle.kts` of the root project
2. `./gradlew clean build publishAllPublicationsToCentralRepository`

# Release Notes

- 0.4 Fixed description of maven artifacts 
- 0.3 Fixed typos in method names
- 0.2 Initial release

# License
```
Copyright 2023 Sergej Shafarenka, www.halfbit.de

You are free to
  - copy and redistribute the material in any medium or format;
  - remix, transform, and build upon the material;
  - use the material or its derivative for commercial purposes.

Any distributed derivative work containing this material or parts 
of it must have this copyright attribution notices.

The material is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
OR CONDITIONS OF ANY KIND, either express or implied.

Contact the developer if you want to use the material under a 
different license.
```
