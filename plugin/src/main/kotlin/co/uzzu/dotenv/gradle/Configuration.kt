package co.uzzu.dotenv.gradle

import org.gradle.StartParameter
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties

internal interface Configuration {
    val filename: String
    val templateFilename: String
}

internal interface RootConfiguration : Configuration {
    val ignoreParentFilename: Boolean
    val ignoreParentTemplateFilename: Boolean
    val experimentalPreferredCliArgs: Boolean
}

@Suppress("ConstPropertyName")
internal object ConfigurationKey {
    const val Filename: String = RootConfigurationKey.Filename
    const val TemplateFilename: String = RootConfigurationKey.TemplateFilename
}

@Suppress("ConstPropertyName")
internal object RootConfigurationKey {
    const val IgnoreParentFilename: String = "dotenv.filename.ignore.parent"
    const val IgnoreParentTemplateFilename: String = "dotenv.template.filename.ignore.parent"
    const val ExperimentalPreferredCliArgs: String = "dotenv.experimental.preferred.cli.args"

    const val Filename: String = "dotenv.filename"
    const val TemplateFilename: String = "dotenv.template.filename"
}

internal object DefaultConfiguration : Configuration {
    override val filename: String = DefaultRootConfiguration.filename
    override val templateFilename: String = DefaultRootConfiguration.templateFilename
}

internal object DefaultRootConfiguration : RootConfiguration {
    override val ignoreParentFilename: Boolean = true
    override val ignoreParentTemplateFilename: Boolean = true
    override val experimentalPreferredCliArgs: Boolean = false

    override val filename: String = ".env"
    override val templateFilename: String = ".env.template"
}

internal class ConfigurationResolver(
    private val project: Project,
    private val startParameter: StartParameter,
) {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private val rootConfiguration: RootConfiguration by lazy { createRootConfiguration() }

    fun resolve(): Configuration {
        return if (project == project.rootProject) {
            rootConfiguration
        } else {
            val gradlePropertiesFromFile = project.gradlePropertiesFromFile()
            val filenameResolver = FilenameResolver(
                project,
                startParameter,
                gradlePropertiesFromFile,
            )
            ConfigurationImpl(
                filename = filenameResolver.resolve(
                    key = ConfigurationKey.Filename,
                    defaultValue = DefaultConfiguration.filename,
                    ignoreParent = rootConfiguration.ignoreParentFilename,
                    experimentalPreferredCliArgs = rootConfiguration.experimentalPreferredCliArgs,
                ),
                templateFilename = filenameResolver.resolve(
                    key = ConfigurationKey.TemplateFilename,
                    defaultValue = DefaultConfiguration.templateFilename,
                    ignoreParent = rootConfiguration.ignoreParentTemplateFilename,
                    experimentalPreferredCliArgs = rootConfiguration.experimentalPreferredCliArgs,
                ),
            )
        }
    }

    private fun createRootConfiguration(): RootConfiguration =
        project.rootProject.let {
            val gradlePropertiesFromFile = it.gradlePropertiesFromFile()
            val filenameResolver = FilenameResolver(
                project,
                startParameter,
                gradlePropertiesFromFile,
            )
            val ignoreParentFilename = it.boolProperty(
                RootConfigurationKey.IgnoreParentFilename,
                DefaultRootConfiguration.ignoreParentFilename,
                logger,
            )
            val ignoreParentTemplateFilename = it.boolProperty(
                RootConfigurationKey.IgnoreParentTemplateFilename,
                DefaultRootConfiguration.ignoreParentTemplateFilename,
                logger,
            )
            val experimentalPreferredCliArgs = it.boolProperty(
                RootConfigurationKey.ExperimentalPreferredCliArgs,
                DefaultRootConfiguration.experimentalPreferredCliArgs,
                logger,
            )
            RootConfigurationImpl(
                ignoreParentFilename = ignoreParentFilename,
                ignoreParentTemplateFilename = ignoreParentTemplateFilename,
                experimentalPreferredCliArgs = experimentalPreferredCliArgs,
                filename = filenameResolver.resolve(
                    key = RootConfigurationKey.Filename,
                    defaultValue = DefaultRootConfiguration.filename,
                    ignoreParent = ignoreParentFilename,
                    experimentalPreferredCliArgs = experimentalPreferredCliArgs,
                ),
                templateFilename = filenameResolver.resolve(
                    key = RootConfigurationKey.TemplateFilename,
                    defaultValue = DefaultRootConfiguration.templateFilename,
                    ignoreParent = ignoreParentTemplateFilename,
                    experimentalPreferredCliArgs = experimentalPreferredCliArgs,
                ),
            )
        }
}

private class FilenameResolver(
    private val project: Project,
    private val startParameter: StartParameter,
    private val gradlePropertiesFromFile: Properties,
) {
    private val cliArgumentTaskProjectPaths: List<String> by lazy {
        startParameter.taskRequests
            .flatMap { it.args }
            .map {
                val path = it.substringBeforeLast(":")
                if (!path.startsWith(":")) {
                    ":$path"
                } else {
                    path
                }
            }
    }

    fun resolve(
        key: String,
        defaultValue: String,
        ignoreParent: Boolean,
        experimentalPreferredCliArgs: Boolean,
    ): String {
        val logger = LoggerFactory.getLogger("FilenameResolver")
        fun resolveWithoutCliArgs(): String = resolveStringFor(
            project,
            gradlePropertiesFromFile,
            key,
            defaultValue,
            ignoreParent,
        )

        logger.debug("${RootConfigurationKey.ExperimentalPreferredCliArgs}: $experimentalPreferredCliArgs")
        if (experimentalPreferredCliArgs) {
            if (cliArgumentTaskProjectPaths.isEmpty()) {
                return if (project == project.rootProject) {
                    resolveStringFor(
                        project,
                        gradlePropertiesFromFile,
                        key,
                        defaultValue,
                        ignoreParent = false,
                    )
                } else {
                    resolveWithoutCliArgs()
                }
            }

            val cliArgsValue = startParameter.projectProperties[key] as? String
            logger.debug("[${project.path}] CLI arguments of key $key: $cliArgsValue")
            if (cliArgsValue.isNullOrBlank()) {
                logger.debug("[${project.path}] CLI arguments of key $key: $cliArgsValue")
                return resolveWithoutCliArgs()
            }

            logger.debug("[${project.path}] CLI arguments for tasks $cliArgumentTaskProjectPaths")
            if (!cliArgumentTaskProjectPaths.contains(project.path)) {
                return resolveWithoutCliArgs()
            }

            return cliArgsValue
        } else {
            return if (project == project.rootProject) {
                resolveStringFor(
                    project,
                    gradlePropertiesFromFile,
                    key,
                    defaultValue,
                    ignoreParent = false,
                )
            } else {
                resolveWithoutCliArgs()
            }
        }
    }

    private fun resolveStringFor(
        project: Project,
        gradlePropertiesFromFile: Properties,
        key: String,
        defaultValue: String,
        ignoreParent: Boolean
    ): String = if (ignoreParent) {
        gradlePropertiesFromFile.getProperty(key, defaultValue)
    } else {
        project.stringProperty(key, defaultValue)
    }
}

private data class ConfigurationImpl(
    override val filename: String,
    override val templateFilename: String,
) : Configuration

private data class RootConfigurationImpl(
    override val ignoreParentFilename: Boolean,
    override val ignoreParentTemplateFilename: Boolean,
    override val experimentalPreferredCliArgs: Boolean,
    override val filename: String,
    override val templateFilename: String,
) : RootConfiguration

private fun Project.gradlePropertiesFromFile(): Properties {
    val result = Properties()
    val gradlePropertiesFile = file(Project.GRADLE_PROPERTIES)
    if (gradlePropertiesFile.exists()) {
        gradlePropertiesFile.inputStream().use { result.load(it) }
    }
    return result
}

private fun Project.boolProperty(key: String, defaultValue: Boolean, logger: Logger): Boolean =
    if (properties.containsKey(key)) {
        @Suppress("MoveVariableDeclarationIntoWhen", "RedundantSuppression")
        val value = properties[key] as String
        when (value) {
            "true" -> {
                true
            }

            "false" -> {
                false
            }

            else -> {
                logger.warn(
                    buildString {
                        append("Could not resolve Boolean properties for key $key.")
                        append(""" Expect should be set "true" or "false", but was "$value". """)
                        append(" The plugin uses default value $defaultValue.")
                    }
                )
                defaultValue
            }
        }
    } else {
        defaultValue
    }

private fun Project.stringProperty(key: String, defaultValue: String): String =
    if (properties.containsKey(key)) {
        properties[key] as String
    } else {
        defaultValue
    }

