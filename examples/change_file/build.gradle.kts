plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.1.0"
}

println(env.FOO.value)
