plugins {
    base
    id("co.uzzu.dotenv.gradle") version "2.1.0"
}

println(env.FOO.value)
