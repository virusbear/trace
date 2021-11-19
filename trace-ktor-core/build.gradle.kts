plugins {
    kotlin("jvm")
}

val publish by configurations.getting

dependencies {
    implementation(kotlin("stdlib"))
    publish("io.github.virusbear.trace:trace-core:${rootProject.version}")
    api(project(":trace-core"))
    implementation("io.ktor:ktor-utils-jvm:1.6.3")
    implementation("io.ktor:ktor-http-jvm:1.6.3")
}