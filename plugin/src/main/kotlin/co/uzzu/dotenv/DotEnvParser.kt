package co.uzzu.dotenv

object DotEnvParser {
    private const val newLine = "\n"
    private val newLinesRegex = Regex("""\\n""", option = RegexOption.MULTILINE)
    private val keyValRegex = Regex("""^\s*([\w.-]+)\s*=\s*(.*)?\s*$""")
    private val newLinesMatches = Regex("""\n|\r|\r\n""")

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
                        dequoted.replace(newLinesRegex, newLine)
                    } else {
                        dequoted
                    }
                } else {
                    rawValue.trim()
                }
                rawKey to trimmedValue
            }
            .toMap()
}
