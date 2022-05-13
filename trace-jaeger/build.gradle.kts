plugins {
    kotlin("jvm")
    `library-publishing`
}

dependencies {
    implementation(kotlin("stdlib"))

    api("io.jaegertracing:jaeger-client:1.8.0")
}