val keys = listOf(
    "FOO",
    "BAR",
    "BAZ",
    "QUX"
)

println("""[$name] ${env.FOO.orElse("default_foo")}""")
println("""[$name] ${env.BAR.orNull()}""")
try {
    println("""[$name]: ${env.BAR.value}""")
} catch (e: java.lang.IllegalStateException) {
    println("[$name] example print: ${e.message}")
}
println("""[$name] ${env.QUX.value}""")

// All environment variables which are merged with variables specified in .env files.
print("[$name] #allVariables() (filtered by keys in .env.template and .env): ")
println(env.allVariables().filterKeys { keys.contains(it) })

// All environment variables which are merged with variables specified in .env files includes null.
// The Plugin set key if defined in .env template files, but it could not be retrieved as nullable value entries by using allVariables()
// By using allVariablesOrNull instead of allVariables, it is possible to retrieve all environment variables, including those that are only defined in the .env template (which means their values are null).
env.allVariablesOrNull()
print("[$name] #allVariablesOrNull() (filtered by keys in .env.template and .env): ")
println(env.allVariablesOrNull().filterKeys { keys.contains(it) })
