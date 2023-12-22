import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import kotlin.streams.asSequence
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

plugins {
    base
    alias(libs.plugins.dotenv.gradle)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

val reportMerge by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/detekt-merged-report.sarif"))
}

detektConfigurationRoot(reportMerge)

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detektConfigurationShared(reportMergeTask = reportMerge)
}

// region detekt configuration

fun Project.detektConfigurationRoot(
    reportMergeTask: TaskProvider<ReportMergeTask>,
) {
    val allKtsFiles = allKtsFiles()
    detektConfigurationShared(
        reportMergeTask = reportMergeTask,
        source = allKtsFiles
            + allBuildSrcKtFiles()
            + files("src"),
    )
}

fun Project.detektConfigurationShared(
    reportMergeTask: TaskProvider<ReportMergeTask>,
    source: List<Any> = listOf(files("src")),
    detektConfig: File = rootProject.file("./gradle/detekt.yml"),
) {
    detekt {
        buildUponDefaultConfig = true
        config.setFrom(detektConfig)
        this.source.setFrom(source)
        ignoreFailures = false
        debug = false
        parallel = true
    }

    tasks.withType<Detekt>().configureEach {
        finalizedBy(reportMerge)
        reports.sarif.required = true
    }

    reportMergeTask {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
    }
}

fun Project.allBuildSrcKtFiles(): List<Path> {
    if (!file(rootProject.projectDir.path + "/buildSrc").exists()) {
        return emptyList()
    }
    val ignoringPath = listOf(".git/", "build/", ".gradle/")
    val ktPattern = "glob:**/*.kt"
    val ktsPattern = "glob:**/*.kts"
    val ktMatcher = FileSystems.getDefault().getPathMatcher(ktPattern)
    val ktsMatcher = FileSystems.getDefault().getPathMatcher(ktsPattern)
    val results = Files.walk(Paths.get(rootProject.projectDir.path + "/buildSrc"))
        .asSequence()
        .filter {
            val path = it.toString()
            !ignoringPath.contains(path) &&
                (ktMatcher.matches(it) || ktsMatcher.matches(it))
        }
        .toList()
    return results
}

fun Project.allKtsFiles(): List<Path> {
    val ignoringPath = listOf(".git/", "build/", ".gradle/", "src/")
    val pattern = "glob:**/*.kts"
    val matcher = FileSystems.getDefault().getPathMatcher(pattern)
    val results = Files.walk(Paths.get(projectDir.path))
        .asSequence()
        .filter {
            val path = it.toString()
            !ignoringPath.contains(path) && matcher.matches(it)
        }
        .toList()
    return results
}

// endregion
