plugins {
    kotlin("jvm")
    `library-publishing`
}

val ktor_version: String by project

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":trace-core"))
    api("io.ktor:ktor-utils-jvm:$ktor_version")
    api("io.ktor:ktor-http-jvm:$ktor_version")
}