package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.DotEnvParser
import org.gradle.api.Project
import java.io.IOException
import java.nio.charset.Charset

/**
 * A utility class designed to resolve and manage environment variables defined using dotenv files
 * for a given project and its hierarchical structure, up to the root project.
 *
 * This class aggregates environment configurations from various dotenv files across projects,
 * resolves variable placeholders, and caches the results for efficiency. It ensures that more
 * specific project configurations override those of parent projects.
 *
 * @constructor Creates a new instance with the specified root project.
 * @param project The project from which the root project and dotenv configurations are initialized.
 */
internal class DotEnvResolver(project: Project) {

    private val rootProject: Project
    private val dotenvCache: MutableMap<Project, Map<String, String?>> = mutableMapOf()

    init {
        rootProject = project.rootProject
    }

    /**
     * Resolves and combines dotenv mappings for the provided project and its ancestor projects, up to the root project.
     * This method aggregates environment variable definitions from all relevant projects, ensuring that variables
     * from more specific projects take precedence over those from parent projects.
     *
     * @param project The project for which to resolve and combine dotenv mappings.
     * @return A map where keys are environment variable names and values are the corresponding resolved values.
     */
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

    /**
     * Fetches or initializes a cached dotenv map associated with the current project. This method ensures that
     * all environment variables defined in the dotenv template and source files are properly loaded and cached.
     * It validates that the project being processed corresponds to the root project of the `DotEnvResolver`.
     *
     * @return A map where keys represent environment variable names and values represent resolved environment variable values.
     */
    private fun Project.dotenv(): Map<String, String?> {
        require(this@dotenv.rootProject == this@DotEnvResolver.rootProject)
        val config = ConfigurationResolver(this).resolve()
        if (dotenvCache[this] == null) {
            val dotenvTemplate = dotenvTemplate(config)
            val dotenvSource = dotenvSource(config)
            val variables = dotenvTemplate.keys
                .union(dotenvSource.keys)
                .associateWith { dotenvSource[it] }
            dotenvCache[project] = variables
        }
        return checkNotNull(dotenvCache[project])
    }

    /**
     * Reads and processes a dotenv template file specified in the configuration.
     * This method parses the template content and resolves any placeholders,
     * returning a map of environment variable names and their resolved values.
     * Throws an exception if the specified template file cannot be read.
     *
     * @param config The configuration containing the filename of the dotenv template file.
     * @return A map containing environment variable names as keys and their resolved values as values.
     * @throws IOException If the specified template file cannot be read or accessed.
     */
    private fun Project.dotenvTemplate(config: Configuration): Map<String, String> {
        val filename = config.templateFilename
            .let {
                if (it != DefaultConfiguration.templateFilename) {
                    val templateFile = file(it)
                    if (!templateFile.exists() || !templateFile.canRead()) {
                        throw IOException(
                            buildString {
                                append("Could not read the dotenv template file specified in the gradle.properties.")
                                append(" ${ConfigurationKey.TemplateFilename}: $it,")
                                append(" path: ${templateFile.absolutePath}")
                            }
                        )
                    }
                }
                it
            }
        val parsedDotenv = readText(filename).let(DotEnvParser::parse)
        return DotEnvParser.substitute(parsedDotenv)
    }

    /**
     * Reads the dotenv file specified in the configuration and parses its content into key-value pairs.
     * The parsed values are then processed to resolve placeholders, producing the final environment variable mappings.
     * Throws an exception if the specified dotenv file cannot be read.
     *
     * @param config The configuration containing the filename of the dotenv file to be read.
     * @return A map of environment variable names to their associated values, resolved from the dotenv file.
     * @throws IOException If the specified dotenv file cannot be read.
     */
    private fun Project.dotenvSource(config: Configuration): Map<String, String> {
        val envFilename = config.filename
            .let {
                if (it != DefaultConfiguration.filename) {
                    val envFile = file(it)
                    if (!envFile.exists() || !envFile.canRead()) {
                        throw IOException(
                            buildString {
                                append("Could not read the dotenv file specified in the gradle.properties.")
                                append(" ${ConfigurationKey.Filename}: $it,")
                                append(" path: ${envFile.absolutePath}")
                            }
                        )
                    }
                }
                it
            }

        val parsedDotenv = readText(envFilename).let(DotEnvParser::parse)
        return DotEnvParser.substitute(parsedDotenv)
    }

    /**
     * Reads the contents of a file with the specified name relative to the project directory.
     * If the file does not exist, an empty string is returned.
     *
     * @param filename The name of the file to be read, relative to the project directory.
     * @return The contents of the file as a string, or an empty string if the file does not exist.
     */
    private fun Project.readText(filename: String): String {
        val file = file(filename)
        return if (file.exists()) {
            file.readText(Charset.forName("UTF-8"))
        } else {
            ""
        }
    }
}
