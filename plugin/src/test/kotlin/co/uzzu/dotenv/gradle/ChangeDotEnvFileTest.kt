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
class ChangeDotEnvFileTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun original() {
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
            file(
                ".env.staging",
                """
                HOGE=200
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
    fun changeFileByEnvironmentVariables() {
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
            file(
                ".env.staging",
                """
                HOGE=200
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withEnvironment(mapOf("ENV_FILE" to ".env.staging"))
            .build()

        assertThat(result.output).contains("Result: 200")
    }

    @Test
    fun throwsIfChangedFileNotFound() {
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

        val runner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .withEnvironment(mapOf("ENV_FILE" to ".env.staging"))

        var error: UnexpectedBuildFailure? = null
        try {
            runner.build()
        } catch (e: UnexpectedBuildFailure) {
            error = e
        }
        assertThat(error)
            .isNotNull()
            .messageContains("Could not read the dotenv file specified in the environment variables. ENV_FILE: .env.staging")
    }
}
