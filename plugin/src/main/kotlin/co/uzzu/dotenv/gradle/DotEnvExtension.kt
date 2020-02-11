package co.uzzu.dotenv.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

@Suppress("UnstableApiUsage")
open class DotEnvExtension
internal constructor(
    private val objectFactory: ObjectFactory
) {
    val templateFileName: Property<String> = objectFactory.property { set(".env.template") }
    val settingFileName: Property<String> = objectFactory.property { set(".env") }
}

private inline fun <reified T> ObjectFactory.property(
    block: Property<T>.() -> Unit = {}
): Property<T> = property(T::class.java).apply(block)

