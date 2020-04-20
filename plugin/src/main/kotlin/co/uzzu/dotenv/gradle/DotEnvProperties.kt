package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.EnvProvider

/**
 * This object will be added as extension which named "env" to the Project.
 */
open class DotEnvRoot(
    private val envProvider: EnvProvider,
    private val dotenvMap: Map<String, String?>
) {
    /**
     * @return All environment variables which are merged with variables specified in .env files.
     */
    val allVariables: Map<String, String>
        get() {
            val results = envProvider.getenv().toMutableMap()
            dotenvMap.forEach { (key, value) ->
                if (value != null && results[key] == null) {
                    results[key] = value
                }
            }
            return results.toMap()
        }

    /**
     * @return Indicates an environment variable with specified name is present
     */
    fun isPresent(name: String): Boolean =
        envProvider.getenv()[name]?.let { true }
            ?: dotenvMap[name]?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String) =
        envProvider.getenv()[name]
            ?: dotenvMap[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String, defaultValue: String) =
        envProvider.getenv()[name]
            ?: dotenvMap[name]
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun fetchOrNull(name: String): String? =
        envProvider.getenv()[name]
            ?: dotenvMap[name]
}

/**
 * This object has environment variables defined on .env file.
 * This object will be added as extension which named environment variable name, to the DotEnv object.
 */
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
