plugins {
    base
    kotlin("jvm") version "1.3.61" apply false
    id("org.jlleitschuh.gradle.ktlint") version "9.2.0"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    ignoreFailures.set(true)
}
