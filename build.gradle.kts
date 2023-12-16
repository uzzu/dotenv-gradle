plugins {
    base
    alias(libs.plugins.dotenv.gradle)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

sharedDetektConfiguration()

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    sharedDetektConfiguration()
}

fun Project.sharedDetektConfiguration() {
    detekt {
        config.setFrom(rootProject.file("./gradle/detekt.yml"))
        buildUponDefaultConfig = true
        ignoreFailures = false
        debug = false
        basePath = rootDir.absolutePath
        parallel = true
    }
}
