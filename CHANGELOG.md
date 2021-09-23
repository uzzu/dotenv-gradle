# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

- Update Gradle to 6.9
- Update Kotlin to 1.4.20
- Add changing file feature
    - If environment variable `ENV_FILE` is set, The plugin read a file specified `ENV_FILE` instead of `.env` file.

## Changed

- Update Gradle to 6.6
- Update Kotlin to 1.3.72

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
