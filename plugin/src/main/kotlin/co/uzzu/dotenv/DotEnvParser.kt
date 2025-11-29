package co.uzzu.dotenv

/**
 * Provides functionality to parse environment variable definitions from a string
 * and substitute placeholders within the values using a map of existing key-value pairs.
 */
object DotEnvParser {
    private const val NewLine = "\n"
    private val newLinesRegex = Regex("""\\n""", option = RegexOption.MULTILINE)
    private val keyValRegex = Regex("""^\s*([\w.-]+)\s*=\s*(.*)?\s*$""")
    private val newLinesMatches = Regex("""\n|\r|\r\n""")
    private val keyPlaceholderMatcher = Regex("""\$\{([\w.-]+)}""") // search for ${KEY} pattern

    /**
     * Parses a string representation of environment variable definitions into a map.
     * Each line in the input string is expected to follow the format `KEY=VALUE`,
     * and lines starting with ";" or "#" are treated as comments and ignored.
     * Values can optionally be single-quoted or double-quoted.
     *
     * @param text The input string containing environment variable definitions.
     * @return A map where keys represent environment variable names and values represent their associated parsed values.
     */
    fun parse(text: String): Map<String, String> =
        text
            .split(newLinesMatches)
            .asSequence()
            .filter { !it.trimStart().startsWith(";") && !it.trimStart().startsWith("#") }
            .map { keyValRegex.matchEntire(it) }
            .filterNotNull()
            .map {
                val rawKey = it.destructured.component1()
                val rawValue = it.destructured.component2()
                val isDoubleQuoted =
                    rawValue.length >= 2 && rawValue.first() == '"' && rawValue.last() == '"'
                val isSingleQuoted =
                    rawValue.length >= 2 && rawValue.first() == '\'' && rawValue.last() == '\''
                val trimmedValue = if (isDoubleQuoted || isSingleQuoted) {
                    val dequoted = rawValue.substring(1, rawValue.lastIndex)
                    if (isDoubleQuoted) {
                        dequoted.replace(newLinesRegex, NewLine)
                    } else {
                        dequoted
                    }
                } else {
                    rawValue.trim()
                }
                rawKey to trimmedValue
            }
            .toMap()

    /**
     * Substitutes placeholders in the values of a map with their corresponding values from the map itself.
     * Uses placeholder syntax in the form `${KEY}` to perform substitutions.
     *
     * @param mapped A map where the keys and associated values may contain placeholders.
     * @return A new map with all placeholder values replaced by their corresponding values from the map.
     */
    fun substitute(mapped: Map<String, String>): Map<String, String> =
        mapped
            .asSequence()
            .map { (key, value) ->
                key to substitutePlaceholders(value, mapped)
            }
            .toMap()

    /**
     * Replaces any placeholders in the given string with corresponding values from the provided map.
     * Placeholders are of the format `${KEY}`, where `KEY` corresponds to a map key.
     * If a placeholder does not have a matching key in the map, it is replaced with an empty string.
     *
     * @param value The string containing placeholders to be substituted.
     * @param mapped A map containing key-value pairs, where each key corresponds to a placeholder in the string.
     * @return A new string with all placeholders replaced by their corresponding values from the map.
     */
    private fun substitutePlaceholders(value: String, mapped: Map<String, String>): String {
        var result = value
        val regex = Regex("""\$\{([\w.-]+)}""")
        var matchResult = keyPlaceholderMatcher.find(result)
        while (matchResult != null) {
            val placeholder = matchResult.groupValues[1]
            val replacement = mapped[placeholder] ?: ""
            result = result.replace(matchResult.value, replacement)
            matchResult = regex.find(result)
        }
        return result
    }
}
