# Dotenv Gradle

![master](https://github.com/uzzu/dotenv-gradle/workflows/master/badge.svg) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/co/uzzu/dotenv/gradle/co.uzzu.dotenv.gradle.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradlePluginPortal)](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle)

**Provides a ( `.env`, `.env.template` ) variables into Project extension.**

## How to use

### Setup

Apply this plugin to root project. [See the Gradle Plugin Portal.](https://plugins.gradle.org/plugin/co.uzzu.dotenv.gradle)

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

### [Optional] Create a `.env.template` file for script completion

If a `.env.template` file exists, this plugin refer it to create a environemnt variable properties in the `env` extension.

[See more examples](/examples/basic)

## License

[Apache License 2.0](/LICENSE.txt)

