[![Maven Central](http://img.shields.io/maven-central/v/de.halfbit/csv.svg)](https://central.sonatype.com/artifact/de.halfbit/csv)

# 🗂 CSV ️

This tiny library was written for one of my projects, and then shared with awesome Kotlin community. 

# Usage

There is no documentation for the API which consists of just three methods:
- `parseCsv(csvText: String): Csv`
- `buildCsv { row { value("code") } } : Csv`
- and `Csv.toCsvText(): String`


Feel free to open PRs for features you miss, please remember though keeping API minimalistic, predictable and self-explanatory.

# Dependencies

In gradle/libs.versions.toml
```toml
[versions]
csv = "0.5"

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

- 0.5 Update to Kotlin 1.9.20
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