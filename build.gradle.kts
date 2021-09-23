plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.2.0"
    kotlin("jvm") version "1.4.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

allprojects {
    repositories {
        mavenCentral()
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

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        ignoreFailures.set(true)
    }
}
