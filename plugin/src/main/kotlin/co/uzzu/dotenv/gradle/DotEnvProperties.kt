package co.uzzu.dotenv.gradle

/**
 * open class for ExtensionAware
 */
open class DotEnvRoot(private val map: Map<String, String>) {
    fun fetch(name: String) =
        map[name]
            ?: System.getenv()[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    fun fetch(name: String, defaultValue: String) =
        map[name]
            ?: System.getenv()[name]
            ?: defaultValue

    fun fetchOrNull(name: String): String? =
        map[name]
            ?: System.getenv()[name]
}

/**
 * open class for ExtensionAware
 */
open class DotEnvProperty(private val name: String, private val value: String?) {
    fun get(): String =
        value
            ?: System.getenv()[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    fun orElse(defaultValue: String): String =
        value
            ?: System.getenv()[name]
            ?: defaultValue

    fun orNull(): String? =
        value
            ?: System.getenv()[name]

}
