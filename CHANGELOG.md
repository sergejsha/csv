# Changelog

## 1.0 - 2025.06.xx

- Update to Kotlin 2.2.0
- Change: Final API refinement (incompatible, see readme and tests for usage examples)

## 0.17 - 2025-05-04
### Fixed
- Issue #24 (Improve headers and rows detection)

### Changed
- Csv.parseText is deprecated. Csv.parseCsvText() should be used instead.

## Older changes

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
