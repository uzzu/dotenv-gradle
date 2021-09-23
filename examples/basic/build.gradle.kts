plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.2.0"
}

println(env.FOO.orElse("default_foo"))
println(env.BAR.orNull())
try {
    println(env.BAZ.value)
} catch (e: java.lang.IllegalStateException) {
    println("example print: ${e.message}")
}
println(env.QUX.value)

// All environment variables which are merged with variables specified in .env files.
env.allVariables
