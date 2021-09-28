plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    api("io.opentracing:opentracing-api:0.33.0")
    api("io.opentracing:opentracing-util:0.33.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}