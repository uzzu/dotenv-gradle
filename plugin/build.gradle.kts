plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm")
    id("com.gradle.plugin-publish")
}

dependencies {
    compileOnly(gradleApi())
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform()
    }
}

// region publishing

object Artifact {
    val groupId = "co.uzzu.dotenv"
    val artifactId = "gradle"
    val version = "3.0.0"
}

group = Artifact.groupId
version = Artifact.version

publishing {
    publishing {
        repositories {
            mavenLocal()
        }

        publications.create("pluginMaven", MavenPublication::class) {
            artifactId = Artifact.artifactId
        }
    }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/uzzu/dotenv-gradle"
    vcsUrl = "https://github.com/uzzu/dotenv-gradle.git"

    plugins {
        create("dotenv") {
            id = "co.uzzu.dotenv.gradle"
            implementationClass = "co.uzzu.dotenv.gradle.DotEnvPlugin"
            displayName = "Gradle dotenv plugin"
            description = "A converting plugin from dotenv(.env.template, .env) files to Gradle project extension"
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
