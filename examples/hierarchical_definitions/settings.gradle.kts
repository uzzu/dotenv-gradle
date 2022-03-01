pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "hierarchical_definitions"
include("sub1", "sub2")
