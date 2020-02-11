plugins {
    java
    `java-gradle-plugin`
    `maven-publish`
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

gradlePlugin {
    plugins {
        register("dotenv") {
            id = "co.uzzu.dotenv.gradle"
            implementationClass = "co.uzzu.dotenv.gradle.DotEnvPlugin"
        }
    }
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

group = "co.uzzu.dotenv"
version = "1.0-SNAPSHOT"

publishing {
    repositories {
        mavenLocal()
    }

    publications.create("pluginMaven", MavenPublication::class) {
        artifactId = "gradle"
    }
}
