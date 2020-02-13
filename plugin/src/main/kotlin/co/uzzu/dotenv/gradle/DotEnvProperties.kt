package co.uzzu.dotenv.gradle

open class DotEnvRoot(private val map: Map<String, String>) {
    /**
     * @return Indicates an environment variable with specified name is present
     */
    fun isPresent(name: String): Boolean =
        System.getenv()[name]?.let { true }
            ?: map[name]?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String) =
        System.getenv()[name]
            ?: map[name]
            ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    fun fetch(name: String, defaultValue: String) =
        System.getenv()[name]
            ?: map[name]
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun fetchOrNull(name: String): String? =
        System.getenv()[name]
            ?: map[name]
}

open class DotEnvProperty(private val name: String, private val dotenvValue: String?) {

    /**
     * @return Indicates an environment variable is present
     */
    val isPresent: Boolean
        get() = System.getenv()[name]?.let { true }
            ?: dotenvValue?.let { true }
            ?: false

    /**
     * @return An environment variable
     * @throws IllegalStateException if it was not set
     */
    val value: String
        get() =
            System.getenv()[name]
                ?: dotenvValue
                ?: throw IllegalStateException("""Environment variable $name was not set.""")

    /**
     * @return An environment variable. If it was not set, returns specified default value
     */
    fun orElse(defaultValue: String): String =
        System.getenv()[name]
            ?: dotenvValue
            ?: defaultValue

    /**
     * @return An environment variable. If it was not set, returns null.
     */
    fun orNull(): String? =
        System.getenv()[name]
            ?: dotenvValue
}
