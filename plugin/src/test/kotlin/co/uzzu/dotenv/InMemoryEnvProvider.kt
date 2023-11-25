package co.uzzu.dotenv

class InMemoryEnvProvider(
    private val map: Map<String, String?>
) : EnvProvider {
    override fun getenv(name: String): String? = map[name]

    override fun getenv(): Map<String, String> = map
        .mapNotNull { (key, value) -> value?.let { key to it } }
        .toMap()

    override fun getenvOrNull(): Map<String, String?> = map.toMap()
}
