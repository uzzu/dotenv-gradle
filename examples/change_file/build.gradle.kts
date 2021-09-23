plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.2.0"
}

println(env.FOO.value)
