plugins {
    id("com.gradle.plugin-publish") version "0.10.1"
    `java-gradle-plugin`
    //`maven-publish`
    kotlin("jvm") version "1.3.61"
}

dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.6.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
    }
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

gradlePlugin {
    plugins {
        register("dotenv") {
            id = "co.uzzu.dotenv.gradle"
            implementationClass = "co.uzzu.dotenv.gradle.DotEnvPlugin"
        }
    }
}

object Artifact {
    val groupId = "co.uzzu.dotenv"
    val artifactId = "gradle"
    val version = "1.0.2"
}

group = Artifact.groupId
version = Artifact.version

pluginBundle {
    website = "https://github.com/uzzu/dotenv-gradle"
    vcsUrl = "https://github.com/uzzu/dotenv-gradle.git"
    description = "A converting plugin from dotenv(.env.template, .env) files to Gradle project extension"
    tags = listOf("dotenv")

    (plugins) {
        "dotenv" {
            displayName = "Gradle dotenv plugin"
            version = Artifact.version
        }
    }

    mavenCoordinates {
        groupId = Artifact.groupId
        artifactId = Artifact.artifactId
        version = Artifact.version
    }
}

// region For testing temporally
//publishing {
//    publishing {
//        repositories {
//            mavenLocal()
//        }
//
//        publications.create("pluginMaven", MavenPublication::class) {
//            artifactId = Artifact.artifactId
//        }
//    }
//}
// endregion
