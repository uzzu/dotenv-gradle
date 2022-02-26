package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.DotEnvParser
import co.uzzu.dotenv.EnvProvider
import co.uzzu.dotenv.SystemEnvProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.IOException
import java.nio.charset.Charset

@Suppress("unused")
class DotEnvPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        check(target == target.rootProject) { "This plugin must be applied to root project." }

        val envProvider = SystemEnvProvider()
        val dotenvTemplate = target.rootProject.dotenvTemplate()
        val dotenvSource = target.rootProject.dotenvSource()
        val dotenvMerged = dotenvTemplate.keys
            .union(dotenvSource.keys)
            .map { it to dotenvSource[it] }
            .toMap()

        target.applyEnv(envProvider, dotenvMerged)
        target.subprojects { it.applyEnv(envProvider, dotenvMerged) }
    }

    private fun Project.applyEnv(envProvider: EnvProvider, dotenvProperties: Map<String, String?>) {
        val env =
            extensions.create("env", DotEnvRoot::class.java, envProvider, dotenvProperties) as ExtensionAware
        dotenvProperties.forEach { (name, value) ->
            env.extensions.create(name, DotEnvProperty::class.java, envProvider, name, value)
        }
    }

    private fun Project.dotenvTemplate(filename: String = ".env.template"): Map<String, String> =
        readText(filename).let(DotEnvParser::parse)

    private fun Project.dotenvSource(filename: String = ".env"): Map<String, String> {
        val envFilename = System.getenv(Companion.KEY_ENV_FILE)
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
