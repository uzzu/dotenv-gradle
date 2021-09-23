package co.uzzu.dotenv.gradle

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

internal fun RootProject(testProjectDir: File, block: TestingRootProject.() -> Unit) {
    TestingRootProject(testProjectDir).apply(block)
}

internal fun TestingRootProject.settingsGradle(content: String = ""): File =
    writeFile("settings.gradle.kts", content)

internal fun TestingRootProject.settingsGradleGroovy(content: String = ""): File =
    writeFile("settings.gradle", content)

internal fun TestingRootProject.buildGradle(content: String = ""): File =
    writeFile("build.gradle.kts", content)

internal fun TestingRootProject.buildGradleGroovy(content: String = ""): File =
    writeFile("build.gradle", content)

internal fun TestingRootProject.file(name: String, content: String): File =
    writeFile(name, content)

internal fun TestingRootProject.directory(name: String): File =
    makeDirectory(name)

internal class TestingRootProject(
    private val projectDir: File
) {
    @Throws(IOException::class)
    internal fun writeFile(name: String, content: String): File {
        val filePath = Paths.get(projectDir.absolutePath, name).normalize()
        val file = filePath.parent.toFile()
        val parentDir = filePath.parent.toFile()
        if (parentDir.exists()) {
            if (!parentDir.isDirectory) {
                throw IOException(
                    """
                    Parent file is not a directory.
                    Writing file path: ${file.absolutePath}
                    Writing file's parent directory path: ${parentDir.absolutePath}
                    """.trimIndent()
                )
            }
        } else {
            Files.createDirectories(Paths.get(parentDir.absolutePath))
        }
        return File(projectDir, name).apply { writeText(content) }
    }

    @Throws(IOException::class)
    internal fun makeDirectory(name: String): File =
        Files.createDirectories(Paths.get(projectDir.absolutePath, name).normalize()).toFile()
}
