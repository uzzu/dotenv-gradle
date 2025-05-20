package co.uzzu.dotenv.gradle

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ResourceInjectionTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun pluginReplacesPlaceholdersInResourceFiles() {
        RootProject(projectDir) {
            settingsGradle()
            buildGradle(
                """
                plugins {
                    java
                    id("co.uzzu.dotenv.gradle")
                }
                """.trimIndent()
            )
            file(
                "config/.env.example",
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
            file(
                "gradle.properties",
                """
                dotenv.filename=config/.env.example
                """.trimIndent()
            )
            file(
                "src/main/resources/test.yml",
                """
                FOO=${'$'}{BAR}
                BAR=${'$'}{FOO}
                """.trimIndent()
            )
            file(
                "src/main/resources/test.properties",
                """
                FOO=${'$'}{FOO}
                BAR=${'$'}{BAR}
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("processResources")
            .forwardOutput()
            .build()

        assertThat(result.task(":processResources")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(projectDir.resolve("build/resources/main/test.yml").readText())
            .contains(
                """
                FOO=bar
                BAR=foo
                """.trimIndent()
            )
        assertThat(projectDir.resolve("build/resources/main/test.properties").readText())
            .contains(
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
    }

    @Test
    fun pluginReplacesPlaceholdersInResourceFilesMatchingFilePattern() {
        RootProject(projectDir) {
            settingsGradle()
            buildGradle(
                """
                plugins {
                    java
                    id("co.uzzu.dotenv.gradle")
                }
                """.trimIndent()
            )
            file(
                ".env.example",
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
            file(
                "gradle.properties",
                """
                dotenv.filename=.env.example
                dotenv.resources.pattern=**/*.properties
                """.trimIndent()
            )
            file(
                "src/main/resources/test.yml",
                """
                FOO=${'$'}{BAR}
                BAR=${'$'}{FOO}
                """.trimIndent()
            )
            file(
                "src/main/resources/test.properties",
                """
                FOO=${'$'}{FOO}
                BAR=${'$'}{BAR}
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("processResources")
            .forwardOutput()
            .build()

        assertThat(result.task(":processResources")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(projectDir.resolve("build/resources/main/test.yml").readText())
            .contains(
                """
                FOO=${'$'}{BAR}
                BAR=${'$'}{FOO}
                """.trimIndent()
            )
        assertThat(projectDir.resolve("build/resources/main/test.properties").readText())
            .contains(
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
    }
    @Test
    fun pluginReplacesPlaceholdersInResourceFilesMatchingFilePatternForCustomSourceSets() {
        RootProject(projectDir) {
            settingsGradle()
            buildGradle(
                """
                plugins {
                    java
                    id("co.uzzu.dotenv.gradle")
                }
                sourceSets {
                    named("main") {
                        resources.srcDirs("src/main/resources", "config/foobar")
                    }
                }
                """.trimIndent()
            )
            file(
                ".env.example",
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
            file(
                "gradle.properties",
                """
                dotenv.filename=.env.example
                dotenv.resources.pattern=**/*.properties
                """.trimIndent()
            )
            file(
                "src/main/resources/test.yml",
                """
                FOO=${'$'}{BAR}
                BAR=${'$'}{FOO}
                """.trimIndent()
            )
            file(
                "config/foobar/test.properties",
                """
                FOO=${'$'}{FOO}
                BAR=${'$'}{BAR}
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withArguments("processResources","--stacktrace","--debug")
            .forwardOutput()
            .build()

        assertThat(result.task(":processResources")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(projectDir.resolve("build/resources/main/test.yml").readText())
            .contains(
                """
                FOO=${'$'}{BAR}
                BAR=${'$'}{FOO}
                """.trimIndent()
            )
        assertThat(projectDir.resolve("build/resources/main/test.properties").readText())
            .contains(
                """
                FOO=foo
                BAR=bar
                """.trimIndent()
            )
    }
}
