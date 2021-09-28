plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":trace-ktor-core"))
    implementation("io.ktor:ktor-client-core:1.6.3")
}
