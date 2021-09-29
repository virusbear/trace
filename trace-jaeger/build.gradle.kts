plugins {
    kotlin("jvm")
}

extra["artifactId"] = "trace-jaeger"
extra["description"] = "JaegerTracing client Tracer Builder DSL"

dependencies {
    implementation(kotlin("stdlib"))
    api("io.jaegertracing:jaeger-client:1.6.0")
}

apply(from = "$rootDir/publishing.gradle.kts")