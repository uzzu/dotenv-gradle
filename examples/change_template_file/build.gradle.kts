plugins {
    base
    id("co.uzzu.dotenv.gradle") version "2.1.0"
}

val keys = listOf(
    "FOO",
    "BAR",
    "BAZ",
)

println(env.allVariablesOrNull().filterKeys { keys.contains(it) })
