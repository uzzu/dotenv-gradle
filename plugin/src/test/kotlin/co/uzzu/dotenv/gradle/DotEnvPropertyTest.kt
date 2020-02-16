package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.InMemoryEnvProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DotEnvPropertyTest {

    @Test
    fun isPresentByEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val property = DotEnvProperty(envProvider, "FOO", null)
        assertTrue(property.isPresent)
    }

    @Test
    fun isPresentByDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertTrue(property.isPresent)
    }

    @Test
    fun isNotPresent() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", null)
        assertFalse(property.isPresent)
    }

    @Test
    fun valueFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("env", property.value)
    }

    @Test
    fun valueFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("dotenv", property.value)
    }

    @Test
    fun throwIfNoVariables() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", null)
        assertThrows(IllegalStateException::class.java) { property.value }
    }

    @Test
    fun valueOrElseFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("env", property.value)
    }

    @Test
    fun valueOrElseFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("dotenv", property.value)
    }

    @Test
    fun valueOrElse() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", null)
        assertEquals("default", property.orElse("default"))
    }

    @Test
    fun fetchOrNullFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("env", property.orNull())
    }

    @Test
    fun fetchOrNullFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", "dotenv")
        assertEquals("dotenv", property.orNull())
    }

    @Test
    fun fetchOrNull() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val property = DotEnvProperty(envProvider, "FOO", null)
        assertNull(property.orNull())
    }
}
