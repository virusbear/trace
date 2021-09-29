plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":trace-core"))
    implementation("io.ktor:ktor-utils-jvm:1.6.3")
    implementation("io.ktor:ktor-http-jvm:1.6.3")
}