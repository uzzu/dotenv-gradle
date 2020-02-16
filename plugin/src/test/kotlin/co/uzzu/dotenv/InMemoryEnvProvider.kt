package co.uzzu.dotenv

class InMemoryEnvProvider(
    private val map: Map<String, String>
) : EnvProvider {
    override fun getenv(name: String): String? = map[name]
    override fun getenv(): Map<String, String> = map
}
