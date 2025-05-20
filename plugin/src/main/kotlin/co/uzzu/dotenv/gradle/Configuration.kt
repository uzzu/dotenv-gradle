package co.uzzu.dotenv.gradle

import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.util.Properties

internal interface Configuration {
    val filename: String
    val templateFilename: String
    val resourcesPattern: String
}

internal interface RootConfiguration : Configuration {
    val ignoreParentFilename: Boolean
    val ignoreParentTemplateFilename: Boolean
    val ignoreParentResourcesPattern: Boolean
}

@Suppress("ConstPropertyName")
object ConfigurationKey {
    const val Filename: String = RootConfigurationKey.Filename
    const val TemplateFilename: String = RootConfigurationKey.TemplateFilename
    const val ResourcesPattern: String = RootConfigurationKey.ResourcesPattern
}

@Suppress("ConstPropertyName")
object RootConfigurationKey {
    const val IgnoreParentFilename: String = "dotenv.filename.ignore.parent"
    const val IgnoreParentTemplateFilename: String = "dotenv.template.filename.ignore.parent"
    const val IgnoreParentResourcesPattern: String = "dotenv.resources.pattern.ignore.parent"

    const val Filename: String = "dotenv.filename"
    const val TemplateFilename: String = "dotenv.template.filename"
    const val ResourcesPattern: String = "dotenv.resources.pattern"
}

internal object DefaultConfiguration : Configuration {
    override val filename: String = DefaultRootConfiguration.filename
    override val templateFilename: String = DefaultRootConfiguration.templateFilename
    override val resourcesPattern: String = DefaultRootConfiguration.resourcesPattern
}

internal object DefaultRootConfiguration : RootConfiguration {
    override val ignoreParentFilename: Boolean = true
    override val ignoreParentTemplateFilename: Boolean = true
    override val ignoreParentResourcesPattern: Boolean = true

    override val filename: String = ".env"
    override val templateFilename: String = ".env.template"
    override val resourcesPattern: String = "**/*"
}

internal class ConfigurationResolver(
    private val project: Project,
) {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    private val rootConfiguration: RootConfiguration by lazy { createRootConfiguration() }

    fun resolve(): Configuration {
        return if (project == project.rootProject) {
            rootConfiguration
        } else {
            val gradlePropertiesFromFile = project.gradlePropertiesFromFile()
            ConfigurationImpl(
                filename = resolveStringFor(
                    project,
                    gradlePropertiesFromFile,
                    ConfigurationKey.Filename,
                    DefaultRootConfiguration.filename,
                    rootConfiguration.ignoreParentFilename,
                ),
                templateFilename = resolveStringFor(
                    project,
                    gradlePropertiesFromFile,
                    ConfigurationKey.TemplateFilename,
                    DefaultRootConfiguration.templateFilename,
                    rootConfiguration.ignoreParentTemplateFilename,
                ),
                resourcesPattern = resolveStringFor(
                    project,
                    gradlePropertiesFromFile,
                    ConfigurationKey.ResourcesPattern,
                    DefaultRootConfiguration.resourcesPattern,
                    rootConfiguration.ignoreParentResourcesPattern,
                ),
            )
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

    private fun createRootConfiguration(): RootConfiguration =
        project.rootProject.let {
            RootConfigurationImpl(
                ignoreParentFilename = it.boolProperty(
                    RootConfigurationKey.IgnoreParentFilename,
                    DefaultRootConfiguration.ignoreParentFilename,
                ),
                ignoreParentTemplateFilename = it.boolProperty(
                    RootConfigurationKey.IgnoreParentTemplateFilename,
                    DefaultRootConfiguration.ignoreParentTemplateFilename,
                ),
                ignoreParentResourcesPattern = it.boolProperty(
                    RootConfigurationKey.IgnoreParentResourcesPattern,
                    DefaultRootConfiguration.ignoreParentResourcesPattern,
                ),
                filename = it.stringProperty(
                    RootConfigurationKey.Filename,
                    DefaultRootConfiguration.filename,
                ),
                templateFilename = it.stringProperty(
                    RootConfigurationKey.TemplateFilename,
                    DefaultRootConfiguration.templateFilename,
                ),
                resourcesPattern = it.stringProperty(
                    RootConfigurationKey.ResourcesPattern,
                    DefaultRootConfiguration.resourcesPattern,
                ),
            )
        }

    private fun Project.gradlePropertiesFromFile(): Properties {
        val result = Properties()
        val gradlePropertiesFile = file(Project.GRADLE_PROPERTIES)
        if (gradlePropertiesFile.exists()) {
            gradlePropertiesFile.inputStream().use { result.load(it) }
        }
        return result
    }

    private fun Project.stringProperty(key: String, defaultValue: String): String =
        if (properties.containsKey(key)) {
            properties[key] as String
        } else {
            defaultValue
        }

    private fun Project.boolProperty(key: String, defaultValue: Boolean): Boolean =
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
                    this@ConfigurationResolver.logger.warn(
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
}

private data class ConfigurationImpl(
    override val filename: String,
    override val templateFilename: String,
    override val resourcesPattern: String,
) : Configuration

private data class RootConfigurationImpl(
    override val ignoreParentFilename: Boolean,
    override val ignoreParentTemplateFilename: Boolean,
    override val ignoreParentResourcesPattern: Boolean,
    override val filename: String,
    override val templateFilename: String,
    override val resourcesPattern: String,
) : RootConfiguration
