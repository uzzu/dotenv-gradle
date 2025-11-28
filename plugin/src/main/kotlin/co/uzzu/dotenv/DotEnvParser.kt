package co.uzzu.dotenv

object DotEnvParser {
    private const val NewLine = "\n"
    private val newLinesRegex = Regex("""\\n""", option = RegexOption.MULTILINE)
    private val keyValRegex = Regex("""^\s*([\w.-]+)\s*=\s*(.*)?\s*$""")
    private val newLinesMatches = Regex("""\n|\r|\r\n""")
    private val keyPlaceholderMatcher = Regex("""\$\{([\w.-]+)}""") // search for ${KEY} pattern

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

    fun substitute(mapped: Map<String, String>): Map<String, String> =
        mapped
            .asSequence()
            .map { (key, value) ->
                key to substitutePlaceholders(value, mapped)
            }
            .toMap()

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
