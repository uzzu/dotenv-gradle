plugins {
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    compileOnly(gradleApi())
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertk)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.toolchain.get().toInt()))
    }
}

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.toVersion(libs.versions.java.toolchain.get()).toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.toVersion(libs.versions.java.toolchain.get()).toString()
    }
    test {
        useJUnitPlatform()
    }
}

// region publishing

object Artifact {
    const val GroupId = "io.github.s41u.dotenv"
    const val ArtifactId = "gradle"
    const val Version = "4.0.0-with-resources"
}

group = Artifact.GroupId
version = Artifact.Version

publishing {
    publishing {
        repositories {
            mavenLocal()
        }

        publications.create("pluginMaven", MavenPublication::class) {
            artifactId = Artifact.ArtifactId
        }
    }
}

gradlePlugin {
    website = "https://github.com/s41u/dotenv-gradle"
    vcsUrl = "https://github.com/s41u/dotenv-gradle.git"

    plugins {
        create("dotenv") {
            id = "${Artifact.GroupId}.${Artifact.ArtifactId}"
            implementationClass = "co.uzzu.dotenv.gradle.DotEnvPlugin"
            displayName = "Dotenv Gradle Plugin with Resources"
            description = "Forked version with resources processing feature"
            tags = listOf("dotenv")
        }
    }
}

if (env.PUBLISH_PRODUCTION.isPresent) {
    val setPublishingSecrets by tasks.creating {
        doLast {
            System.setProperty("gradle.publish.key", env.GRADLE_PUBLISH_KEY.value)
            System.setProperty("gradle.publish.secret", env.GRADLE_PUBLISH_SECRET.value)
        }
    }
    tasks.getByName("publishPlugins").dependsOn(setPublishingSecrets)
}

// endregion
