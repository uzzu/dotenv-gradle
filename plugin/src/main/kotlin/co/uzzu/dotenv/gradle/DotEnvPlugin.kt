package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.EnvProvider
import co.uzzu.dotenv.SystemEnvProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

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
    }
}
