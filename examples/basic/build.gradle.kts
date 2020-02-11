plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.0.0"
}

println(env.FOO.orElse("default_foo"))
println(env.BAR.orNull())
try {
    println(env.BAZ.value)
} catch (e: java.lang.IllegalStateException) {
    println("example print: ${e.message}")
}
println(env.QUX.value)

