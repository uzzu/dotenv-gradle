plugins {
    base
    id("co.uzzu.dotenv.gradle") version "3.0.0"
    kotlin("jvm") version "1.4.31" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
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
