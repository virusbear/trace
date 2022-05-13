plugins {
    kotlin("jvm")
    `library-publishing`
}

val ktor_version: String by project

dependencies {
    implementation(kotlin("stdlib"))

    api(project(":trace-ktor-core"))
    api("io.ktor:ktor-client-core:$ktor_version")
}