plugins {
    base
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

println("[$name] FOO: ${env.FOO.value}")
println("""[$name] BAR is present: ${env.isPresent("BAR")}""")
println("""[$name] BAZ is present: ${env.isPresent("BAZ")}""")
