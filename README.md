# Dotenv Gradle

![main](https://github.com/uzzu/dotenv-gradle/workflows/main/badge.svg) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/co/uzzu/dotenv/gradle/co.uzzu.dotenv.gradle.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradlePluginPortal)](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle)

**Provides dotenv (`.env`) and dotenv templates (`.env.template`) as variables in a Project extension.**

## How to use

### Setup

Apply this plugin to the root project. [Read the Gradle Plugin Portal to setup the plugin.](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle).

Note that this plugin is not registered to Maven Central.

You do not need to apply this plugin to subprojects; the values are applied automatically.

### Create `.env` in the root directory of your gradle project

For example:

```dosini
FOO=foo
BAR="bar"
BAZ='baz'

# You can comment out lines with a #
; You can comment out lines with a ;
```

Then, you will be able to use the environment variables in your gradle scripts:

```Kotlin
println(env.FOO.isPresent)              // => true
println(env.FOO.value)                  // => foo
println(env.BAR.orNull())               // => bar
println(env.BAZ.orElse("default baz"))  // => baz
```

**Don't commit the `.env` file.** Ideally, add it to your `.gitignore` file.

### API

- `(Boolean) env.isPresent(name: String)`: Indicates that an environment variable with specified name is present.

- `(String) env.fetch(name: String)`: Returns the environment variable of the given name.

- `(String) env.fetch(name: String, defaultValue: String)`: Returns an environment variable, or specified default value if environment variable was not set.

- `(String?) env.fetchOrNull(name: String)`: Returns an environment variable, or `null` if the environment variable was not set.

- `(Map<String, String) env.allVariables()`: Returns all environment variables.

- `(Map<String, String?) env.allVariablesOrNull()`: Returns all environment variables, and includes `null` values for unset variables.

### Templates

If a `.env.template` file exists, this plugin populates environment variables from it, too. This means you can use the template to define the environment variables that are required for your project, and optionally override them in the `.env` file.

[See the example](/examples/change_template_file) for more details.

### Changing the name of the `.env` file

You can also use a different name for the `.env` file. [See this example](/examples/change_file) on how to do this.

### Hierarchical dotenv definitions

This project supports subproject-only variables and extensions.

[See this example](/examples/hierarchical_definitions) for more details.

### Other Features/Functions

Note that all APIs of this `env` extension consider the `.env` file.

If the same variable name value is defined in both the `.env` file and system environment variables, the system environment variable takes precedence.

[See more examples](/examples).

## Restrictions

This plugin does not support specifying properties with the `-P` option of CLI arguments, and there are no plans to support it in the future. See [#67](https://github.com/uzzu/dotenv-gradle/issues/67)

## License

[Apache License 2.0](/LICENSE.txt)
