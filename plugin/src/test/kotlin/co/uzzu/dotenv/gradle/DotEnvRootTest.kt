package co.uzzu.dotenv.gradle

import co.uzzu.dotenv.InMemoryEnvProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class DotEnvRootTest {
    @Test
    fun isPresentByEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val root = DotEnvRoot(envProvider, mapOf())
        assertTrue(root.isPresent("FOO"))
    }

    @Test
    fun isPresentByDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertTrue(root.isPresent("FOO"))
    }

    @Test
    fun isNotPresent() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf())
        assertFalse(root.isPresent("FOO"))
    }

    @Test
    fun fetchFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("env", root.fetch("FOO"))
    }

    @Test
    fun fetchFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("dotenv", root.fetch("FOO"))
    }

    @Test
    fun throwIfNoVariables() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf())
        assertThrows(IllegalStateException::class.java) { root.fetch("FOO") }
    }

    @Test
    fun fetchOrElseFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("env", root.fetch("FOO", "default"))
    }

    @Test
    fun fetchOrElseFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("dotenv", root.fetch("FOO", "default"))
    }

    @Test
    fun fetchOrElse() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf())
        assertEquals("default", root.fetch("FOO", "default"))
    }

    @Test
    fun fetchOrNullFromEnv() {
        val envProvider = InMemoryEnvProvider(mapOf("FOO" to "env"))
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("env", root.fetch("FOO", "default"))
    }

    @Test
    fun fetchOrNullFromDotEnv() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf("FOO" to "dotenv"))
        assertEquals("dotenv", root.fetch("FOO", "default"))
    }

    @Test
    fun fetchOrNull() {
        val envProvider = InMemoryEnvProvider(mapOf())
        val root = DotEnvRoot(envProvider, mapOf())
        assertNull(root.fetchOrNull("FOO"))
    }

    @Test
    fun allVariables() {
        val envProvider = InMemoryEnvProvider(
            mapOf(
                "FOO" to "env",
                "BAR" to "env"
            )
        )
        val root = DotEnvRoot(
            envProvider,
            mapOf(
                "BAR" to "dotenv",
                "BAZ" to "dotenv",
                "QUX" to "dotenv",
                "QUUX" to null
            )
        )
        val allVariables = root.allVariables()
        assertAll(
            { assertEquals("env", allVariables["FOO"]) },
            { assertEquals("env", allVariables["BAR"]) },
            { assertEquals("dotenv", allVariables["BAZ"]) },
            { assertEquals("dotenv", allVariables["QUX"]) },
            { assertEquals(null, allVariables["QUUX"]) },
            { assertEquals(null, allVariables["CORGE"]) }
        )
    }

    @Test
    fun allVariablesOrNull() {
        val envProvider = InMemoryEnvProvider(
            mapOf(
                "FOO" to "env",
                "BAR" to "env",
                "CORGE" to null,
            )
        )
        val root = DotEnvRoot(
            envProvider,
            mapOf(
                "BAR" to "dotenv",
                "BAZ" to "dotenv",
                "QUX" to "dotenv",
                "QUUX" to null,
            )
        )
        val allVariables = root.allVariablesOrNull()
        assertAll(
            { assertEquals("env", allVariables["FOO"]) },
            { assertEquals("env", allVariables["BAR"]) },
            { assertEquals("dotenv", allVariables["BAZ"]) },
            { assertEquals("dotenv", allVariables["QUX"]) },
            { assertEquals(null, allVariables["QUUX"]) },
            { assertTrue(allVariables.containsKey("QUUX")) },
            { assertEquals(null, allVariables["CORGE"]) },
            { assertTrue(allVariables.containsKey("CORGE")) },
        )
    }
}
