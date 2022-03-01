package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.DotEnvParser
import org.gradle.api.Project
import java.io.IOException
import java.nio.charset.Charset

internal class DotEnvResolver(project: Project) {

    private val rootProject: Project
    private val dotenvCache: MutableMap<Project, Map<String, String?>> = mutableMapOf()

    init {
        rootProject = project.rootProject
    }

    fun resolve(project: Project): Map<String, String?> {
        val dotenvList = mutableListOf<Map<String, String?>>()

        var current = project
        while (true) {
            val dotenv = current.dotenv()
            dotenvList.add(0, dotenv)
            if (current == rootProject) {
                break
            }
            if (current.parent == null) {
                break
            }
            current = checkNotNull(current.parent)
        }
        return dotenvList
            .fold(mutableMapOf<String, String?>()) { destination, dotenv -> destination.apply { putAll(dotenv) } }
            .toMap()
    }

    private fun Project.dotenv(): Map<String, String?> {
        require(this@dotenv.rootProject == this@DotEnvResolver.rootProject)
        if (dotenvCache[this] == null) {
            val dotenvTemplate = dotenvTemplate()
            val dotenvSource = dotenvSource()
            val variables = dotenvTemplate.keys
                .union(dotenvSource.keys)
                .associateWith { dotenvSource[it] }
            dotenvCache[project] = variables
        }
        return checkNotNull(dotenvCache[project])
    }

    private fun Project.dotenvTemplate(filename: String = ".env.template"): Map<String, String> =
        readText(filename).let(DotEnvParser::parse)

    private fun Project.dotenvSource(filename: String = ".env"): Map<String, String> {
        val envFilename = System.getenv(KEY_ENV_FILE)
            ?.takeIf {
                val envFile = file(it)
                if (!envFile.exists() || !envFile.canRead()) {
                    throw IOException(
                        buildString {
                            append("Could not read the dotenv file specified in the environment variables.")
                            append(" $KEY_ENV_FILE: $it,")
                            append(" path: ${envFile.absolutePath}")
                        }
                    )
                }
                true
            }
            ?: filename
        return readText(envFilename).let(DotEnvParser::parse)
    }

    private fun Project.readText(filename: String): String {
        val file = file(filename)
        return if (file.exists()) {
            file.readText(Charset.forName("UTF-8"))
        } else {
            ""
        }
    }

    companion object {
        private const val KEY_ENV_FILE = "ENV_FILE"
    }
}
