plugins {
    base
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

val keys = listOf(
    "FOO",
    "BAR",
    "BAZ",
)

println(env.allVariablesOrNull().filterKeys { keys.contains(it) })
