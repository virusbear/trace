plugins {
    kotlin("jvm")
}

extra["artifactId"] = "trace-jaeger"
extra["description"] = "JaegerTracing client Tracer Builder DSL"

val publish by configurations.getting

dependencies {
    implementation(kotlin("stdlib"))
    publish("io.jaegertracing:jaeger-client:1.6.0")
    api("io.jaegertracing:jaeger-client:1.6.0")
}

apply(from = "$rootDir/publishing.gradle.kts")