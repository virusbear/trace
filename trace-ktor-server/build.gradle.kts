plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":trace-ktor-core"))
    implementation("io.ktor:ktor-server-core:1.6.3")
}
