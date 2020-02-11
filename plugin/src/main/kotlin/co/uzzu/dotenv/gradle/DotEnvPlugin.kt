package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.DotEnvParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.nio.charset.Charset

@Suppress("unused")
class DotEnvPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val envTemplate = target.rootProject.envTemplate()
        val envSource = target.rootProject.envSource()
        val envMerged = envTemplate.keys
            .union(envSource.keys)
            .map { it to envSource[it] }
            .toMap()

        target.applyEnv(envMerged)
        target.subprojects { it.applyEnv(envMerged) }
    }

    private fun Project.applyEnv(envProperties: Map<String, String?>) {
        val env =
            extensions.create("env", DotEnvRoot::class.java, envProperties) as ExtensionAware
        envProperties.forEach { (name, value) ->
            env.extensions.create(name, DotEnvProperty::class.java, name, value)
        }
    }

    private fun Project.envTemplate(filename: String = ".env.template") =
        readText(filename).let(DotEnvParser::parse)

    private fun Project.envSource(filename: String = ".env") =
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
