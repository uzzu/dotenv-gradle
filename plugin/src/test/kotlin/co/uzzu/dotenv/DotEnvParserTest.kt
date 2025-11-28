package co.uzzu.dotenv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class DotEnvParserTest {

    @Test
    fun testBasicParse() {
        val text = """
        HOGE_API_KEY="dummy_key"
        HOGE_API_SECRET="dummy_secret"
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        assertAll(
            { assertEquals(actual["HOGE_API_KEY"], "dummy_key") },
            { assertEquals(actual["HOGE_API_SECRET"], "dummy_secret") }
        )
    }

    @Test
    fun testBasicSubstitutions() {
        val text = """
        HOGE_API_KEY="dummy_key"
        HOGE_API_SECRET="dummy_secret"
        BASIC_AUTH="Basic: ${'$'}{HOGE_API_KEY}:${'$'}{HOGE_API_SECRET}"
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        val substituted = DotEnvParser.substitute(actual)
        assertAll(
            { assertEquals(substituted["HOGE_API_KEY"], "dummy_key") },
            { assertEquals(substituted["HOGE_API_SECRET"], "dummy_secret") },
            { assertEquals(substituted["BASIC_AUTH"], "Basic: dummy_key:dummy_secret") }
        )
    }

    @Test
    fun emptyValue() {
        val text = """
        HOGE_API_KEY=
        HOGE_API_SECRET=
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        assertAll(
            { assertEquals(actual["HOGE_API_KEY"], "") },
            { assertEquals(actual["HOGE_API_SECRET"], "") }
        )
    }

    @Test
    fun quotedValue() {
        val text = """
        HOGE_API_KEY="dummy_key"
        HOGE_API_SECRET='dummy_secret'
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        assertAll(
            { assertEquals(actual["HOGE_API_KEY"], "dummy_key") },
            { assertEquals(actual["HOGE_API_SECRET"], "dummy_secret") }
        )
    }

    @Test
    fun incompleteQuoteValue() {
        val text = """
        HOGE_API_KEY="dummy_key
        HOGE_API_SECRET=dummy_secret'
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        assertAll(
            { assertEquals(actual["HOGE_API_KEY"], """"dummy_key""") },
            { assertEquals(actual["HOGE_API_SECRET"], """dummy_secret'""") }
        )
    }

    @Test
    fun singleCharQuoteValue() {
        val text = """
        HOGE_API_KEY="
        HOGE_API_SECRET='
        """.trimIndent()

        val actual = DotEnvParser.parse(text)
        assertAll(
            { assertEquals(actual["HOGE_API_KEY"], """"""") },
            { assertEquals(actual["HOGE_API_SECRET"], """'""") }
        )
    }
}
