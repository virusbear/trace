plugins {
    kotlin("jvm")
}

extra["artifactId"] = "trace-ktor-server"
extra["description"] = "ktor Server feature for OpenTracing API"

val bundle by configurations.getting

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":trace-ktor-core"))
    bundle(project(":trace-ktor-core"))
    implementation("io.ktor:ktor-server-core:1.6.3")
}

apply(from = "$rootDir/publishing.gradle.kts")