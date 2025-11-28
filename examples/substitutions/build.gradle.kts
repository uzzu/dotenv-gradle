plugins {
    base
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

val keys = listOf(
    "APP_NAME",
    "APP_DESCRIPTOR_CLASSNAME",
    "PROVIDED_XML_FILES_LOCATION"
)

println(env.APP_NAME.orElse("my_first_app"))
println(env.APP_DESCRIPTOR_CLASSNAME.orNull())
println(env.PROVIDED_XML_FILES_LOCATION.value)

// All environment variables which are merged with variables specified in .env files.
print("#allVariables() (filtered by keys in .env.template and .env): ")
println(env.allVariables().filterKeys { keys.contains(it) })

// All environment variables which are merged with variables specified in .env files includes null.
// The Plugin set key if defined in .env template files, but it could not be retrieved as nullable value entries by using allVariables()
// By using allVariablesOrNull instead of allVariables, it is possible to retrieve all environment variables, including those that are only defined in the .env template (which means their values are null).
env.allVariablesOrNull()
print("#allVariablesOrNull() (filtered by keys in .env.template and .env): ")
println(env.allVariablesOrNull().filterKeys { keys.contains(it) })
