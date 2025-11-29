package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.EnvProvider
import co.uzzu.dotenv.SystemEnvProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * The DotEnvPlugin class is a custom Gradle plugin designed to handle environment variables
 * for a project. This plugin enables loading and configuring environment variables
 * from dotenv files or the system into a Gradle project's configuration. It ensures
 * that environment variables are available to the build process and subprojects as needed.
 *
 * This plugin must be applied only to the root project of the build.
 *
 * Features:
 * - Loads environment variables from `.env` files or system environment variables.
 * - Makes the variables accessible via the Gradle `env` extension.
 * - Propagates the environment variable configurations to subprojects in the build.
 *
 * Responsibilities:
 * - Validates that the plugin is applied only to the root project.
 * - Initializes the environment provider and resolver.
 * - Resolves and applies environment variable configurations to the root project and its subprojects.
 *
 * Behavior:
 * When applied, the plugin creates an `env` extension in the root project and subprojects.
 * This extension encapsulates the resolved environment variables, making them accessible
 * to the build scripts. Each variable can be addressed individually and configured using
 * the DotEnvProperty extension.
 */
@Suppress("unused")
class DotEnvPlugin : Plugin<Project> {
    /**
     * Applies the DotEnvPlugin to the specified Gradle project. This plugin ensures that
     * environment variables are resolved and added to the project and all its subprojects.
     *
     * @param target The project to which the plugin is applied. Must be the root project.
     * @throws IllegalArgumentException If the plugin is applied to a non-root project.
     */
    override fun apply(target: Project) {
        check(target == target.rootProject) { "This plugin must be applied to root project." }

        val envProvider = SystemEnvProvider()
        val resolver = DotEnvResolver(target)
        val rootVariables = resolver.resolve(target)

        target.applyEnv(envProvider, rootVariables)
        target.subprojects { it.applyEnv(envProvider, resolver.resolve(it)) }
    }

    /**
     * Adds environment variables as extensions to the Project.
     *
     * @param envProvider Provides methods to access environment variables.
     * @param dotenvProperties A map of environment variable names to their values. Values can be null.
     */
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
