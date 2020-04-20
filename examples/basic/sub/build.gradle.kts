println("""[$name] ${env.FOO.orElse("default_foo")}""")
println("""[$name] ${env.BAR.orNull()}""")
try {
    println("""[$name]: ${env.BAR.value}""")
} catch (e: java.lang.IllegalStateException) {
    println("[$name] example print: ${e.message}")
}
println("""[$name] ${env.QUX.value}""")

// All environment variables which are merged with variables specified in .env files.
env.allVariables
