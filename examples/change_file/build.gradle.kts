plugins {
    base
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

println(env.FOO.value)
