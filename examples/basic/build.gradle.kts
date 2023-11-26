plugins {
    base
    id("co.uzzu.dotenv.gradle") version "3.0.0"
}

val keys = listOf(
    "FOO",
    "BAR",
    "BAZ",
    "QUX"
)

println(env.FOO.orElse("default_foo"))
println(env.BAR.orNull())
try {
    println(env.BAZ.value)
} catch (e: java.lang.IllegalStateException) {
    println("example print: ${e.message}")
}
println(env.QUX.value)

// All environment variables which are merged with variables specified in .env files.
print("#allVariables() (filtered by keys in .env.template and .env): ")
println(env.allVariables().filterKeys { keys.contains(it) })

// All environment variables which are merged with variables specified in .env files includes null.
// The Plugin set key if defined in .env template files, but it could not be retrieved as nullable value entries by using allVariables()
// By using allVariablesOrNull instead of allVariables, it is possible to retrieve all environment variables, including those that are only defined in the .env template (which means their values are null).
env.allVariablesOrNull()
print("#allVariablesOrNull() (filtered by keys in .env.template and .env): ")
println(env.allVariablesOrNull().filterKeys { keys.contains(it) })
