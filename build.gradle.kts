plugins {
    kotlin("jvm") version "1.5.21" apply false
}

group = "io.github.virusbear.trace"
version = "1.0"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
}