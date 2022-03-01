println("[${name}] FOO: ${env.FOO.value} (override)")
println("""[${name}] BAR is present: ${env.isPresent("BAR")}""")
println("[${name}] BAZ: ${env.BAZ.value}")
