package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.EnvProvider
import co.uzzu.dotenv.SystemEnvProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import org.gradle.language.jvm.tasks.ProcessResources

@Suppress("unused")
class DotEnvPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        check(target == target.rootProject) { "This plugin must be applied to root project." }

        val envProvider = SystemEnvProvider()
        val resolver = DotEnvResolver(target)
        val rootVariables = resolver.resolve(target)

        target.applyEnv(envProvider, rootVariables)
        target.subprojects { it.applyEnv(envProvider, resolver.resolve(it)) }
    }

    private fun Project.applyEnv(envProvider: EnvProvider, dotenvProperties: Map<String, String?>) {
        val env = extensions.create(
            "env",
            DotEnvRoot::class.java,
            envProvider,
            dotenvProperties
        ) as ExtensionAware
        dotenvProperties.forEach { (name, value) ->
            env.extensions.create(name, DotEnvProperty::class.java, envProvider, name, value)
        }

        applyEnvToResources(dotenvProperties)
    }

    private fun Project.applyEnvToResources(dotenvProperties: Map<String, String?>) {
        plugins.withType(JavaPlugin::class.java) {
            val pr = project.tasks.findByName("processResources")
            if (pr is ProcessResources) {
                pr.duplicatesStrategy = DuplicatesStrategy.INCLUDE
                val configuration = ConfigurationResolver(this).resolve()
                pr.inputs.properties(dotenvProperties)
                pr.filesMatching(configuration.resourcesPattern) { details -> details.expand(dotenvProperties) }
            }
        }
    }
}
