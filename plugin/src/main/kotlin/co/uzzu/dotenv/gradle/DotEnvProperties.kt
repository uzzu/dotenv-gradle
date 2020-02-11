package co.uzzu.dotenv.gradle

open class DotEnvRoot(private val map: Map<String, String>) {
    /**
     * @return Indicates an environment variable with specified name is present
     */
    fun isPresent(name: String): Boolean =
        map[name]?.let { true }
            ?: System.getenv()[name]?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String) =
        map[name]
            ?: System.getenv()[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String, defaultValue: String) =
        map[name]
            ?: System.getenv()[name]
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun fetchOrNull(name: String): String? =
        map[name]
            ?: System.getenv()[name]
}

open class DotEnvProperty(private val name: String, private val rawValue: String?) {

    /**
     * @return Indicates an environment variable is present
     */
    val isPresent: Boolean
        get() = rawValue?.let { true }
            ?: System.getenv()[name]?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    val value: String
        get() = rawValue
            ?: System.getenv()[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun orElse(defaultValue: String): String =
        rawValue
            ?: System.getenv()[name]
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns null.
     */
    fun orNull(): String? =
        rawValue
            ?: System.getenv()[name]

}
