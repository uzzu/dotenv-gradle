package co.uzzu.dotenv

/**
 * Provides environment variable
 */
interface EnvProvider {
    /**
     * @return A environment variable for name.
     */
    fun getenv(name: String): String?

    /**
     * @return All environment variables.
     */
    fun getenv(): Map<String, String>

    /**
     * @return All environment variables includes null values.
     */
    fun getenvOrNull(): Map<String, String?>
}

/**
 * EnvProvider implementation using System#getenv
 */
class SystemEnvProvider : EnvProvider {
    override fun getenv(name: String): String? = System.getenv(name)

    override fun getenv(): Map<String, String> = System.getenv()
        .mapNotNull { (key, value) -> value?.let { key to it } }
        .toMap()

    override fun getenvOrNull(): Map<String, String?> = System.getenv()
}
