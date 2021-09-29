plugins {
    kotlin("jvm") version "1.5.30" apply false
}

group = "io.github.virusbear.trace"
version = "1.0.0"

allprojects {
    extra["groupId"] = rootProject.group
    extra["version"] = rootProject.version

    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
}