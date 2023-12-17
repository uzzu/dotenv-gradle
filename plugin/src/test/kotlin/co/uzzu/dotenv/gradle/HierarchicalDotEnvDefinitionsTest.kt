package co.uzzu.dotenv.gradle

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.contains
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class HierarchicalDotEnvDefinitionsTest {

    @TempDir
    lateinit var projectDir: File

    @Test
    fun subProjectOnlyVariables() {
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
                println("[root] present FUGA: ${'$'}{env.isPresent("FUGA")}")
                """.trimIndent()
            )
            directory("sub")
            file(
                "sub/build.gradle",
                """
                println("[sub] present FUGA: ${'$'}{env.isPresent("FUGA")}")
                """.trimIndent()
            )
            file(
                "sub/.env.template",
                """
                FUGA=
                """.trimIndent()
            )
            file(
                "sub/.env",
                """
                FUGA=200
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .build()

        assertAll {
            assertThat(result.output).contains("[root] present FUGA: false")
            assertThat(result.output).contains("[sub] present FUGA: true")
        }
    }

    @Test
    fun useSubProjectVariablesIfDuplicatedWithRootProject() {
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
                println("[root] HOGE: ${'$'}{env.HOGE.value}")
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
            directory("sub")
            file(
                "sub/build.gradle",
                """
                println("[sub] HOGE: ${'$'}{env.HOGE.value}")
                """.trimIndent()
            )
            file(
                "sub/.env.template",
                """
                HOGE=
                """.trimIndent()
            )
            file(
                "sub/.env",
                """
                HOGE=200
                """.trimIndent()
            )
        }

        val result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir)
            .build()

        assertAll {
            assertThat(result.output).contains("[root] HOGE: 100")
            assertThat(result.output).contains("[sub] HOGE: 200")
        }
    }
}
