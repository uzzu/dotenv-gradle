package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.DotEnvParser
import co.uzzu.dotenv.EnvProvider
import co.uzzu.dotenv.SystemEnvProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.nio.charset.Charset

@Suppress("unused")
class DotEnvPlugin : Plugin<Project> {
    override fun apply(target: Project) {
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

    private fun Project.dotenvSource(filename: String = ".env"): Map<String, String> =
        readText(filename).let(DotEnvParser::parse)

    private fun Project.readText(filename: String): String {
        val file = file(filename)
        return if (file.exists()) {
            file.readText(Charset.forName("UTF-8"))
        } else {
            ""
        }
    }
}
