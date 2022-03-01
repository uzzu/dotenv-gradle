package co.uzzu.dotenv.gradle

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotNull
import assertk.assertions.messageContains
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@Suppress("UnstableApiUsage") // GradleRunner#withPluginClasspath
class BasicUsageTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun applyToRootProject() {
        RootProject(projectDir) {
            settingsGradle()
            buildGradle(
                """
                plugins {
                    base
                    id("co.uzzu.dotenv.gradle")
                }
                println("Result: ${'$'}{env.HOGE.value}")
                """.trimIndent()
            )
            file(
                ".env.template",
                """
                HOGE=
                """.trimIndent()
            )
            file(
                ".env",
                """
                HOGE=100
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .build()

        assertThat(result.output).contains("Result: 100")
    }

    @Test
    fun applyToSubProject() {
        RootProject(projectDir) {
            settingsGradle(
                """
                include("sub")
                """.trimIndent()
            )
            buildGradle(
                """
                plugins {
                    base
                    id("co.uzzu.dotenv.gradle")
                }
                println("Result: ${'$'}{env.HOGE.value}")
                """.trimIndent()
            )
            file(
                ".env.template",
                """
                HOGE=
                """.trimIndent()
            )
            file(
                ".env",
                """
                HOGE=100
                """.trimIndent()
            )
            file(
                ".env.staging",
                """
                HOGE=200
                """.trimIndent()
            )
            directory("sub")
            file(
                "sub/build.gradle",
                """
                println("[Sub] Result: ${'$'}{env.HOGE.value}")
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .build()

        assertThat(result.output).contains("Result: 100")
        assertThat(result.output).contains("[Sub] Result: 100")
    }

    @Test
    fun throwIfAppliedSubProject() {
        RootProject(projectDir) {
            settingsGradle(
                """
                include("sub")
                """.trimIndent()
            )
            buildGradle(
                """
                plugins {
                    base
                }
                """.trimIndent()
            )
            directory("sub")
            file(
                "sub/build.gradle",
                """
                plugins {
                    id("co.uzzu.dotenv.gradle")
                }
                """.trimIndent()
            )
        }

        val runner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)

        var error: UnexpectedBuildFailure? = null
        try {
            runner.build()
        } catch (e: UnexpectedBuildFailure) {
            error = e
        }
        assertThat(error)
            .isNotNull()
            .messageContains("This plugin must be applied to root project.")
    }
}
