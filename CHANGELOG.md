# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

## [2.1.0] - 2023-11-26

### Added

- `(Map<String, String?>) env.allVariablesOrNull()` to get all environment variables includes variables specified
  in `.env` includes null variables.
  - The Plugin set key if defined in .env template files, but it could not be retrieved as nullable value entries by
    using allVariables()
  - By using allVariablesOrNull instead of allVariables, it is possible to retrieve all environment variables, including
    those that are only defined in the .env template (which means their values are null).
- Change `.env.template` filename feature use gradle property `dotenv.template.filename`
  - Almost cases are work fine, but has similler problem with [#39](https://github.com/uzzu/dotenv-gradle/issues/39)

### Deprecated

- `val (Map<String, String>) env.allVariables`
  - Replace to use a method `(Map<String, String>) env.allVariables()` instead.

## [2.0.0] - 2022-03-02

### Added

- Hierarchical dotenv definitions support [#19](https://github.com/uzzu/dotenv-gradle/issues/19)
  - [See example](/examples/hierarchical_definitions)

### Changed

- Change `.env` filename feature use gradle property `dotenv.filename` , instead of environment variable `ENV_FILE`
  . [#20](https://github.com/uzzu/dotenv-gradle/issues/20)
  - [See migration guide](/examples/change_file/README.md#Migrate-from-1x)
- Update Gradle to 7.1.1 [#16](https://github.com/uzzu/dotenv-gradle/issues/16)
- Update Kotlin to 1.4.31 used into Gradle 7.1.1

## [1.2.0] - 2021-09-24

## Added

- Changing `.env` file [#14](https://github.com/uzzu/dotenv-gradle/issues/14)
  - If environment variable `ENV_FILE` is set, The plugin read a file specified `ENV_FILE` instead of `.env` file.

## Changed

- Update Gradle to 6.9
- Update Kotlin to 1.4.20

## [1.1.0] - 2020-04-21

### Added

- `(Map<String, String>) env.allVariables` to get all environment variables includes variables specified in `.env`
  files. [#5](https://github.com/uzzu/dotenv-gradle/pull/5)

## [1.0.2] - 2020-02-12

### Fixed

- Fix incorrect resolving order.

## [1.0.1] - 2020-02-12

### Fixed

- Fix gradle plugin metadata.

## [1.0.0] - 2020-02-12

### Added

- Initial release.
