import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30" apply false
}

group = "io.github.virusbear.trace"
version = "1.0.1"

allprojects {
    extra["groupId"] = rootProject.group
    extra["version"] = rootProject.version

    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }

    tasks.withType<KotlinCompile> {
        sourceCompatibility = "11"
    }
}