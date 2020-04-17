package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.EnvProvider

open class DotEnvRoot(
    private val envProvider: EnvProvider,
    private val map: Map<String, String>
) {
    /**
     * @return Indicates an environment variable with specified name is present
     */
    fun isPresent(name: String): Boolean =
        envProvider.getenv()[name]?.let { true }
            ?: map[name]?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String) =
        envProvider.getenv()[name]
            ?: map[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String, defaultValue: String) =
        envProvider.getenv()[name]
            ?: map[name]
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun fetchOrNull(name: String): String? =
        envProvider.getenv()[name]
            ?: map[name]
}

open class DotEnvProperty(
    private val envProvider: EnvProvider,
    private val name: String,
    private val dotenvValue: String?
) {

    /**
     * @return Indicates an environment variable is present
     */
    val isPresent: Boolean
        get() = envProvider.getenv()[name]?.let { true }
            ?: dotenvValue?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    val value: String
        get() =
            envProvider.getenv()[name]
                ?: dotenvValue
                ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun orElse(defaultValue: String): String =
        envProvider.getenv()[name]
            ?: dotenvValue
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns null.
     */
    fun orNull(): String? =
        envProvider.getenv()[name]
            ?: dotenvValue
}
