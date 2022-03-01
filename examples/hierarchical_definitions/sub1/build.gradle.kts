println("[${name}] FOO: ${env.FOO.value}")
println("[${name}] BAR: ${env.BAR.value}")
println("""[${name}] BAZ is present: ${env.isPresent("BAZ")}""")
