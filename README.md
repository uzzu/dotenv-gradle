# Dotenv Gradle

** Provides a ( `.env`, `.env.template` ) variables into Project extension.

## How to use

### Setup

Apply this plugin to root project.

#### Using buildscript

<details>
<summary>Kotlin DSL</summary>

```Kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("co.uzzu.dotenv:gradle:1.0.0")
    }
}

apply(plugin = "co.uzzu.dotenv.gradle")
```

</details>
<details open>
<summary>Groovy DSL</summary>

```Groovy
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath "co.uzzu.dotenv:gradle:1.0.0"
    }
}

apply plugin: "co.uzzu.dotenv.gradle"
```

</details>


#### Using new plugin API

<details>
<summary>Kotlin DSL</summary>

```Kotlin
plugins {
    id("co.uzzu.dotenv.gradle") version "1.0.0"
}

```

</details>
<details open>
<summary>Groovy DSL</summary>

```Groovy
plugins {
    id "co.uzzu.dotenv.gradle" version "1.0.0"
}
```

</details>

### Create `.env` in the root directory of your gradle project.

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

### [Optional] Create a `.env.template` file for script completion

If a `.env.template` file exists, this plugin refer it to create a environemnt variable properties in the `env` extension.

[See more examples](/examples/basic)

## License

[Apache License 2.0](/LICENSE.txt)

