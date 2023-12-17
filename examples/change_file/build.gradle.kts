plugins {
    base
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

println(env.FOO.value)
