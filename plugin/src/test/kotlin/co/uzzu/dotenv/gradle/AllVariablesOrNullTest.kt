package co.uzzu.dotenv.gradle

import assertk.assertThat
import assertk.assertions.contains
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AllVariablesOrNullTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun nullValuesDefinedByTemplate() {
        RootProject(projectDir) {
            settingsGradle()
            buildGradle(
                """
                plugins {
                    base
                    id("co.uzzu.dotenv.gradle")
                }
                println("Result: ${'$'}{env.allVariablesOrNull().filterKeys { it == "HOGE" }}")
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
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .build()

        assertThat(result.output).contains("Result: {HOGE=null}")
    }
}
