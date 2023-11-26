plugins {
    base
    id("co.uzzu.dotenv.gradle") version "3.0.0"
}

println(env.FOO.value)
