# Dotenv Gradle

![main](https://github.com/uzzu/dotenv-gradle/workflows/main/badge.svg) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/co/uzzu/dotenv/gradle/co.uzzu.dotenv.gradle.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradlePluginPortal)](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle)

**Provides a ( `.env`, `.env.template` ) variables into Project extension.**

## How to use

### Setup

Apply this plugin to root project. This plugin is not registered to Maven Central.
[Read the Gradle Plugin Portal to setup plugin.](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle)

Don't need to apply this plugin to subprojects.

### Create `.env` in the root directory of your gradle project

For example:

```dosini
FOO=foo
BAR="bar"
BAZ='baz'

# starting line comment out supported
; starting line comment out supported
```

Then, you will be able to use environment variable into your gradle scripts.

```Kotlin
println(env.FOO.isPresent)              // => true
println(env.FOO.value)                  // => foo
println(env.BAR.orNull())               // => bar
println(env.BAZ.orElse("default baz"))  // => baz
```

**Don't commit `.env` file**

### Create a `.env.template` file for script completion

If a `.env.template` file exists, this plugin refer it to create a environemnt variable properties in the `env`
extension.

### Change a file to read instead of `.env` file.

If environment variable `ENV_FILE` is set, The plugin read a file specified `ENV_FILE` instead of `.env` file.

[See example](/examples/change_file)

### Hierarchical dotenv definitions

Support subproject-only variables and extensions.

[See example](/examples/hierarchical_definitions)

### Others

- All APIs of `env` extension consider `.env` file.
  - If the same variable name value is defined in both the `.env` file and the system environment variable, the system
    environment variable takes precedence.
- `(Boolean) env.isPresent(name: String)` : Indicates an environment variable with specified name is present.
- `(String) env.fetch(name: String)` : Returns an environment variable.
- `(String) env.fetch(name: String, defaultValue: String)` : Returns an environment variable, or specified default value
  if environment variable was not set.
- `(String?) env.fetchOrNull(name: String)` : Returns an environment variable, or null if environment variable was not
  set.
- `(Map<String, String) env.allVariables` : Returns all environment variables.
- [See more examples](/examples)

## License

[Apache License 2.0](/LICENSE.txt)

