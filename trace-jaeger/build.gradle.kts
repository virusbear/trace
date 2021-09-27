plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    api("io.jaegertracing:jaeger-client:1.6.0")
}
